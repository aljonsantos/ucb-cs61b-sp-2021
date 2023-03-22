package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class CommitTree implements Serializable {
    public static final File TREE = join(Repository.GITLET_DIR, "metadata");
    static final String DEFAULT_BRANCH = "master";
    private Commit head;
    private String branch;
    private TreeMap<String, Commit> branches;

    public CommitTree() {
        Commit initialCommit = Commit.createIntialCommit();
        initialCommit.save();

        branches = new TreeMap<>();
        branches.put(DEFAULT_BRANCH, initialCommit);
        branch = DEFAULT_BRANCH;
        head = branches.get(branch);
    }

    public void newCommit(String message) {
        if (StagingArea.filesForAddition().isEmpty() && StagingArea.filesForRemoval().isEmpty()) {
            exitWithMessage("No changes added to the commit.");
        }
        if (message.equals("")) {
            exitWithMessage("Please enter a commit message.");
        }

        Commit commit = Commit.createCommit(message);
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
            p = p.parents().get(0);
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
        for (String file : StagingArea.filesForAddition()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : StagingArea.filesForRemoval()) {
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

    public void mergeBranch(String branch) {
        if (Repository.tree.branch().equals(branch)) {
            exitWithMessage("Cannot merge a branch with itself.");
        }
        if (!branches.containsKey(branch)) {
            exitWithMessage("A branch with that name does not exist.");
        }
        if (!StagingArea.filesForAddition().isEmpty() || !StagingArea.filesForRemoval().isEmpty()) {
            exitWithMessage("You have uncommitted changes.");
        }

        Commit other = branches.get(branch);
        Commit split = findSplitPoint(head, other);

        if (split.equals(other)) {
            exitWithMessage("Given branch is an ancestor of the current branch.");
        }
        if (split.equals(head)) {
            checkoutBranch(branch);
            exitWithMessage("Current branch fast-forwarded.");
        }

        Set<String> mergeFiles = new TreeSet<>(split.blobs().keySet());
        mergeFiles.addAll(head.blobs().keySet());
        mergeFiles.addAll(other.blobs().keySet());

        boolean isMergeConflict = false;
        HashMap<String, Commit> filesToCheckout = new HashMap<>();
        List<String> filesToRemove = new ArrayList<>();
        HashMap<String, String> conflictFiles = new HashMap<>();

        for (String file : mergeFiles) {
            if (Blob.isModified(file, split, head) && Blob.isModified(file, split, other) && Blob.isModified(file, head, other)) {
                isMergeConflict = true;
                String headContents = head.containsBlob(file) ? new String(Blob.read(head.blobHash(file)).contents()) : "";
                String otherContents = other.containsBlob(file) ? new String(Blob.read(other.blobHash(file)).contents()) : "";

                String contents = String.format("<<<<<<< HEAD\n%s=======\n%s>>>>>>>\n", headContents, otherContents);
                conflictFiles.put(file, contents);

            } else if (split.containsBlob(file)) {
                if (!Blob.isModified(file, split, head)) {
                    if (!other.containsBlob(file)) {
                        filesToRemove.add(file); // case d
                    } else if (Blob.isModified(file, split, other)) { // case a
                        filesToCheckout.put(file, other);
                    }
                }
            } else if (!head.containsBlob(file)) {
                filesToCheckout.put(file, other);
            }
        }

        Set<String> filesToBeOverwritten = new TreeSet<>(plainFilenamesIn(Repository.CWD));
        filesToBeOverwritten.removeAll(head.blobs().keySet());
        filesToBeOverwritten.retainAll(filesToCheckout.keySet());

        if (!filesToBeOverwritten.isEmpty()) {
            exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
        }

        for (String file : filesToRemove) {
            StagingArea.remove(file);
        }
        for (String file : filesToCheckout.keySet()) {
            checkoutFileFromCommit(filesToCheckout.get(file).hash(), file);
            StagingArea.add(file);
        }
        for (String file : conflictFiles.keySet()) {
            writeContents(join(Repository.CWD, file), conflictFiles.get(file));
            StagingArea.add(file);
        }

        Commit commit = Commit.createMergeCommit(branch);
        commit.configBlobs();
        commit.setHash();
        commit.save();

        StagingArea.clear();

        if (isMergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }

        branches.put(Repository.tree.branch(), commit);
        head = commit;

    }

    private Commit findSplitPoint(Commit b1, Commit b2) {
        Set<String> b1CommitHistory = new TreeSet<>();
        while (b1 != null) {
            b1CommitHistory.add(b1.hash());
            if (b1.parents().size() > 1) {
                b1CommitHistory.add(b1.parents().get(1).hash());
            }
            b1 = b1.parent();
        }
        while (b2 != null) {
            if (b1CommitHistory.contains(b2.hash())) {
                return Commit.read(b2.hash());
            }
            b2 = b2.parent();
        }
        return null;
    }

    public String branch() {
        return this.branch;
    }

    public TreeMap<String, Commit> branches() {
        return this.branches;
    }

    public static CommitTree read() {
        return readObject(TREE, CommitTree.class);
    }

    public void save() {
        writeObject(TREE,this);
    }

}
