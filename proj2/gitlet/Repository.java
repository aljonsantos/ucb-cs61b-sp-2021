package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Repository {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    static CommitTree tree;
    static Commit head;

    public static void read() {
        if (isInitialized()) {
            tree = CommitTree.read();
            head = tree.head();
        }
    }

    public static void initialize() {
        if (isInitialized()) {
            Utils.exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }

        GITLET_DIR.mkdir();
        StagingArea.STAGING_DIR.mkdir();
        StagingArea.ADDITION_DIR.mkdir();
        StagingArea.REMOVAL_DIR.mkdir();
        Commit.COMMITS_DIR.mkdir();
        Blob.BLOBS_DIR.mkdir();

        tree = new CommitTree();
        tree.save();
    }

    public static void commit(String message) {
        tree.newCommit(message);
        tree.save();
    }

    public static void log() {
        CommitTree.printLogFrom(head);
    }

    public static void globalLog() {
        CommitTree.printGlobalLog();
    }

    public static void status() {
        tree.printStatus();
    }

    public static void checkout(String[] args) {
        String filename, hash, branch;

        if (args.length == 3 && args[1].equals("--")) {
            filename = args[2];
            tree.checkoutFileFromCommit(head.hash(), filename);
        }
        else if (args.length == 4 && args[2].equals("--")) {
            hash = args[1];
            filename = args[3];
            tree.checkoutFileFromCommit(hash, filename);
        }
        else if (args.length == 2) {
            branch = args[1];
            tree.checkoutBranch(branch);
            tree.save();
        }
        else {
            exitWithMessage("Incorrect Operands.");
        }
    }

    public static void find(String message) {
        CommitTree.find(message);
    }

    public static void branch(String branch) {
        tree.newBranch(branch);
        tree.save();
    }

    public static void remove(String branch) {
        tree.removeBranch(branch);
        tree.save();
    }

    public static void reset(String hash) {
        tree.resetToCommit(hash);
        tree.save();
    }

    public static void merge(String branch) {
        tree.mergeBranch(branch);
        tree.save();
    }

    public static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

}
