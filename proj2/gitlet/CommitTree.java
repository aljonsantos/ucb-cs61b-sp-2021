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

//        deleteFilesIn(Repository.CWD, StagingArea.filesForRemoval);
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

    public void find(String message) {
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

//    public void chekoutFile(String filename) {
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
        else if (!commit.containsBlob(filename)) {
            exitWithMessage("File does not exist in that commit.");
        }
        else {
            Blob.checkout(commit, filename);
        }
    }

    public void checkoutBranch(String branch) {
        if (branch.equals(this.branch)) {
            exitWithMessage("No need to checkout the current branch.");
        }

        Commit current = head;
        Commit other = branches.get(branch);

        if (other == null) {
            exitWithMessage("No such branch exists.");
        }
        else if (!filesToBeOverwritten(current, other).isEmpty()) {
            exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        else {
            Set<String> filesToBeDeleted = new TreeSet<>(current.blobs().keySet());
            filesToBeDeleted.removeAll(other.blobs().keySet());
            deleteFilesIn(Repository.CWD, new ArrayList<>(filesToBeDeleted));

            for (String file : other.blobs().keySet()) {
                Blob.checkout(other, file);
            }

            this.branch = branch;
            head = other;
            StagingArea.clear();
        }
    }

    private Set<String> filesToBeOverwritten(Commit from, Commit to) {
        Set<String> untrackedCWDFiles = new TreeSet<>();
        for (String file : plainFilenamesIn(Repository.CWD)) {
            untrackedCWDFiles.add(new Blob(Repository.CWD, file).hash());
        }
        untrackedCWDFiles.removeAll(from.blobs().values());
        Set<String> commonFiles = new TreeSet<>(to.blobs().values());
        commonFiles.retainAll(untrackedCWDFiles);
        return commonFiles;
    }

    public void newBranch(String branch) {
        if (branches.containsKey(branch)) {
            exitWithMessage("A branch with that name already exists.");
        }
        branches.put(branch, head);
    }

    public static CommitTree read() {
        return readObject(TREE, CommitTree.class);
    }

    public void save() {
        writeObject(TREE,this);
    }

}
