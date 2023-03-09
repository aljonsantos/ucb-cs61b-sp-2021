package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    static CommitTree tree;
    static Commit head;

    /* TODO: fill in the rest of this class. */

    public static void readCommitTree() {
        if (GITLET_DIR.exists()) {
            tree = readObject(CommitTree.TREE, CommitTree.class);
            head = tree.head();
        }
    }


    public static void initialize() {
        if (isGitletInitialized()) {
            Utils.exitWithMessage("A Gitlet version-control system already exists in the current directory.");
        }

        GITLET_DIR.mkdir();

        StagingArea.STAGING_DIR.mkdir();
        StagingArea.ADDITION_DIR.mkdir();
        StagingArea.REMOVAL_DIR.mkdir();

        Commit.COMMITS_DIR.mkdir();
        Blob.BLOBS_DIR.mkdir();

        tree = new CommitTree();
        tree.initialize();
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
            tree.chekoutFile(filename);
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

    public static void branch(String branch) {
        tree.newBranch(branch);
        tree.save();
    }

    public static boolean isGitletInitialized() {
        return Repository.GITLET_DIR.exists();
    }








//    private static boolean isFileIn(File dir, String filename) {
//        List<String> files = plainFilenamesIn(dir);
//        return files != null && files.contains(filename);
//    }
}
