package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import static gitlet.Utils.*;

public class CommitTree implements Serializable {
    public static final File TREE = join(Repository.GITLET_DIR, "metadata");
    static final String DEFAULT_BRANCH = "master";
    private Commit head;
    private String branch;
    private TreeMap<String, Commit> branches;

    public CommitTree() {
        Commit initialCommit = new Commit();
        initialCommit.save();

        branches = new TreeMap<>();
        branches.put(DEFAULT_BRANCH, initialCommit);
        branch = DEFAULT_BRANCH;
        head = branches.get(branch);
    }

    public void newCommit(String message) {
        if (StagingArea.filesForAddition.isEmpty() && StagingArea.filesForRemoval.isEmpty()) {
            exitWithMessage("No changes added to the commit.");
        }
        if (message.equals("")) {
            exitWithMessage("Please enter a commit message.");
        }

        Commit commit = new Commit(message);
        commit.save();

        branches.put(branch, commit);
        head = branches.get(branch);

        StagingArea.clear();
    }

    public Commit head() {
        return this.head;
    }

    public static void printLogFrom(Commit head) {
        Commit p = head;
        while (p != null) {
            System.out.println(p);
//            p.printBlobs();
            p = p.parent();
        }
    }

    public static void printGlobalLog() {
        List<String> hashes = getAllCommits();
        if (hashes != null) {
            for (String hash : hashes) {
                System.out.println(Commit.read(hash));
            }
        }
    }

    public static List<String> getAllCommits() {
        return plainFilenamesIn(Commit.COMMITS_DIR);
    }

    public void printStatus() {
        System.out.println("=== Branches ===");
        for (String key : branches.keySet()) {
            if (key.equals(branch)) {
                System.out.print("*");
            }
            System.out.println(key);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String file : StagingArea.filesForAddition) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : StagingArea.filesForRemoval) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void find(String message) {
        List<String> hashes = getAllCommits();
        boolean found = false;
        for (String hash : hashes) {
            Commit commit = Commit.read(hash);
            if (commit.message().equals(message)) {
                System.out.println(commit.hash());
                found = true;
            }
        }
        if (!found) {
            exitWithMessage("Found no commit with that message.");
        }
    }

//    public void checkoutFile(String filename) {
//        if (!head.containsBlob(filename)) {
//            exitWithMessage("File does not exist in that commit.");
//        }
//        Blob.checkout(head, filename);
//    }

    public void checkoutFileFromCommit(String hash, String filename) {
        Commit commit = Commit.read(hash);
        if (commit == null) {
            exitWithMessage("No commit with that id exists.");
        }
        if (!commit.containsBlob(filename)) {
            exitWithMessage("File does not exist in that commit.");
        }
        Blob.checkout(commit, filename);
    }

    public void checkoutBranch(String branch) {
        if (branch.equals(this.branch)) {
            exitWithMessage("No need to checkout the current branch.");
        }

        Commit other = branches.get(branch);
        if (other == null) {
            exitWithMessage("No such branch exists.");
        }
        if (!filesToBeOverwritten(head, other).isEmpty()) {
            exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        checkoutAllFilesIn(other);
        this.branch = branch;
        head = other;
        StagingArea.clear();
    }

    public void newBranch(String branch) {
        if (branches.containsKey(branch)) {
            exitWithMessage("A branch with that name already exists.");
        }
        branches.put(branch, head);
    }

    public void removeBranch(String branch) {
        if (this.branch.equals(branch)) {
            exitWithMessage("Cannot remove the current branch.");
        }
        if (!branches.containsKey(branch)) {
            exitWithMessage("A branch with that name does not exist.");
        }
        branches.remove(branch);
    }

    public void resetToCommit(String hash) {
        Commit commit = Commit.read(hash);
        if (commit == null) {
            exitWithMessage("No commit with that id exists.");
        }
        if (!filesToBeOverwritten(head, commit).isEmpty()) {
            exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        checkoutAllFilesIn(commit);
        branches.put(branch, commit);
        head = commit;
        StagingArea.clear();
    }

    private Set<String> filesToBeOverwritten(Commit from, Commit to) {
        Set<String> untrackedCWDFiles = new TreeSet<>(plainFilenamesIn(Repository.CWD));
        untrackedCWDFiles.removeAll(from.blobs().keySet());
        Set<String> commonFiles = new TreeSet<>(to.blobs().keySet());
        commonFiles.retainAll(untrackedCWDFiles);
        return commonFiles;
    }

    private void checkoutAllFilesIn(Commit commit) {
        Set<String> filesToBeDeleted = new TreeSet<>(head.blobs().keySet());
        filesToBeDeleted.removeAll(commit.blobs().keySet());
        deleteFilesIn(Repository.CWD, new ArrayList<>(filesToBeDeleted));

        for (String file : commit.blobs().keySet()) {
            Blob.checkout(commit, file);
        }
    }

    public static CommitTree read() {
        return readObject(TREE, CommitTree.class);
    }

    public void save() {
        writeObject(TREE,this);
    }

}
