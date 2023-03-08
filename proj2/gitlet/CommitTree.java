package gitlet;

// TODO: any imports you need here

import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class CommitTree implements Serializable {
    public static final File COMMITS_DIR = join(Repository.GITLET_DIR, "commits");
    public static final File TREE = join(Repository.GITLET_DIR, "metadata");
    static final String INIT_BRANCH = "master";
    private Commit head;
    private String branch;
    private HashMap<String, Commit> branches;


    public CommitTree() {
        head = createInitialCommit();
        branches = new HashMap<>();
        branches.put(INIT_BRANCH, head);
        branch = INIT_BRANCH;
    }

    private Commit createInitialCommit() {
        Commit commit = new Commit();
        commit.save();
        return commit;
    }

    public void createCommit(String message) {
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
        head = commit;
        commit.save();

        StagingArea.clear();

    }
    public void moveHead(Commit commit) {
        this.head = commit;
    }

    static void setHead(CommitTree tree, Commit commit) {
        tree.moveHead(commit);
    }




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
            p.printBlobs();
            p = p.parent();
        }
    }

    public static void printGlobalLog() {
        List<Commit> commits = getAllCommits();
        if (commits != null) {
            for (Commit commit : commits) {
                System.out.println(commit);
            }
        }
    }

//    public static void printLog(Commit commit) {
//        System.out.println("===");
//        System.out.println(commit);
//        System.out.println();
//    }

    public static List<Commit> getAllCommits() {
        List<String> hashes = plainFilenamesIn(CommitTree.COMMITS_DIR);
        if (hashes == null) {
            return null;
        }
        List<Commit> commits = new ArrayList<>();
        for (String hash : hashes) {
            commits.add(Commit.readFromFile(hash));
        }
        return commits;
    }

    public static List<Commit> getAllCommits(String message) {
        List<String> hashes = plainFilenamesIn(CommitTree.COMMITS_DIR);
        if (hashes == null) {
            return null;
        }
        List<Commit> commits = new ArrayList<>();
        for (String hash : hashes) {
            Commit commit = Commit.readFromFile(hash);
            if (commit.message().equals(message)) {
                commits.add(commit);
            }
        }
        return commits;
    }

    public static void find(String message) {
        List<Commit> commits = getAllCommits(message);
        if (commits == null || commits.size() == 0) {
            exitWithMessage("Found no commit with that message.");
        }
        else {
            for (Commit commit: commits) {
                System.out.println(commit.hash());
            }
        }
    }




}
