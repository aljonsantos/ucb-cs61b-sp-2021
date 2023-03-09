package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class CommitTree implements Serializable {
    public static final File TREE = join(Repository.GITLET_DIR, "metadata");
    static final String DEFAULT_BRANCH = "master";
    private Commit head;
    private String branch;
    private TreeMap<String, Commit> branches;


//    public CommitTree() {
//        head = createInitialCommit();
//        branches = new HashMap<>();
//        branches.put(INIT_BRANCH, head.hash());
//        branch = INIT_BRANCH;
//    }

    public void initialize() {
        Commit initialCommit = new Commit();
        initialCommit.save();

//        head = initialCommit;
        branches = new TreeMap<>();
        branches.put(DEFAULT_BRANCH, initialCommit);
        branch = DEFAULT_BRANCH;
        head = branches.get(branch);
    }

//    private Commit createInitialCommit() {
//        Commit commit = new Commit();
//        commit.save();
//        return commit;
//    }


    public void newCommit(String message) {
        List<String> filesForAddition = StagingArea.getFilesForAddtion();
        List<String> filesForRemoval = StagingArea.getFilesForRemoval();

        if (filesForAddition.isEmpty() && filesForRemoval.isEmpty()) {
            exitWithMessage("No changes added to the commit.");
        }
        if (message.equals("")) {
            exitWithMessage("Please enter a commit message.");
        }

        Commit commit = new Commit(message);
        commit.setBlobs(filesForAddition, filesForRemoval);
        commit.setHash();
        commit.save();

        branches.put(branch, commit);
        head = branches.get(branch);

        StagingArea.clear();
    }
//    public void moveHead(Commit commit) {
//        this.head = commit;
//    }

//    public void setHead(Commit commit) {
//        head = commit.hash();
//    }


    public void save() {
        // call on commit command
        writeObject(TREE,this);
//        Commit.readFromFile(head).save();
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
                System.out.println(Commit.readFromFile(hash));
            }
        }
    }

//    public static void printLog(Commit commit) {
//        System.out.println("===");
//        System.out.println(commit);
//        System.out.println();
//    }

    public static List<String> getAllCommits() {
        return plainFilenamesIn(Commit.COMMITS_DIR);
    }

//    public static List<String> getAllCommits(String message) {
//        List<String> hashes = plainFilenamesIn(CommitTree.COMMITS_DIR);
//        if (hashes == null) {
//            return null;
//        }
//        List<Commit> commits = new ArrayList<>();
//        for (String hash : hashes) {
//            Commit commit = Commit.readFromFile(hash);
//            if (commit.message().equals(message)) {
//                commits.add(commit);
//            }
//        }
//        return commits;
//    }

    public static void find(String message) {
        List<String> hashes = getAllCommits();
        boolean found = false;
        for (String hash : hashes) {
            Commit commit = Commit.readFromFile(hash);
            if (commit.message().equals(message)) {
                System.out.println(commit.hash());
            }
            found = true;
        }
        if (!found) {
            exitWithMessage("Found no commit with that message.");
        }
    }

    public List<String> branches() {
        return new ArrayList<>(branches.keySet());
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
        for (String file : StagingArea.getFilesForAddtion()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : StagingArea.getFilesForRemoval()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();


    }

    public void chekoutFile(String filename) {
        if (!head.containsBlob(filename)) {
            exitWithMessage("File does not exist in that commit.");
        }
        Blob.checkout(head, filename);
    }

    public void checkoutFileFromCommit(String hash, String filename) {
        Commit commit = Commit.readFromFile(hash);
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
        Commit commit = branches.get(branch);
        if (commit == null) {
            exitWithMessage("No such branch exists.");
        }
        else if (!filesToBeOverwritten().isEmpty()) {
            exitWithMessage("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        else {
            // delete files in cwd
//            deleteAllFilesIn(Repository.CWD);
            Set<String> filesToBeDeleted = new TreeSet<>(head.blobs().keySet());
            filesToBeDeleted.removeAll(branches.get(branch).blobs().keySet());

            System.out.println("files to be deleted");
            for (String file : new ArrayList<>(filesToBeDeleted)) {
                System.out.println(file);
            }

            deleteFilesIn(Repository.CWD, new ArrayList<>(filesToBeDeleted));
            // bring branch files to cwd
            for (String key : branches.get(branch).blobs().keySet()) {
                Blob.checkout(commit, key);
            }

            this.branch = branch;
            head = branches.get(this.branch);
            StagingArea.clear();
        }
    }

    private Set<String> filesToBeOverwritten() {
        // get files in cwd that are not in staged and check if they are in commit
        // get files in folder1 that are not in folder2 and check if they are in folder3
        Set<String> untrackedCWDFiles = new TreeSet<>(plainFilenamesIn(Repository.CWD));
        untrackedCWDFiles.removeAll(head.blobs().keySet());

//        for (String file : new ArrayList<>(untrackedCWDFiles)) {
//            System.out.println(file);
//        }

        Set<String> commonFiles = new TreeSet<>(branches.get(branch).blobs().keySet());
        commonFiles.retainAll(untrackedCWDFiles);
        return commonFiles;
    }

    public void newBranch(String branch) {
        if (branches.containsKey(branch)) {
            exitWithMessage("A branch with that name already exists.");
        }
        branches.put(branch, head);
    }


}
