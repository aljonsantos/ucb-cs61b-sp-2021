package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

import static gitlet.Repository.head;

public class StagingArea {

    public static final File STAGING_DIR = join(Repository.GITLET_DIR, "staging");
    public static final File ADDITION_DIR = join(STAGING_DIR, "addition");
    public static final File REMOVAL_DIR = join(STAGING_DIR, "removal");

    public static void add(String filename) {
        File f = join(Repository.CWD, filename);
        if (!f.exists()) {
            exitWithMessage("File does not exist.");
        }

        boolean isFileIdenticalToHeads = new Blob(f).hash().equals(head.blobs().get(filename));
        if (!isFileIdenticalToHeads) {
//            System.out.println("added: " + filename);
            stageFile(filename);
            unstageFileForRemoval(filename);
        }
        else {
            unstageFile(filename);
        }
    }

    public static void remove(String filename) {
        if (!head.containsBlob(filename) && !StagingArea.isStaged(filename)) {
            exitWithMessage("No reason to remove the file.");
        }
        else if (head.containsBlob(filename)) {
            stageFileForRemoval(filename);
            deleteFile(Repository.CWD, filename);
        }
        unstageFile(filename);
    }

    static void stageFile(String filename) {
        File f = join(Repository.CWD, filename);
        File copy = join(ADDITION_DIR, filename);
        writeContents(copy, readContents(f));
    }

    static void unstageFile(String filename) {
        deleteFile(ADDITION_DIR, filename);
    }

    static void unstageFileForRemoval(String filename) {
        deleteFile(REMOVAL_DIR, filename);
    }

    static void stageFileForRemoval(String filename) {
        File f = join(Repository.CWD, filename);
        File copy = join(REMOVAL_DIR, filename);
        writeContents(copy, readContents(f));
    }

    static boolean isStaged(String filename) {
        File f = join(ADDITION_DIR, filename);
        return f.exists();
    }

    static void clear() {
        deleteFilesIn(ADDITION_DIR);
        deleteFilesIn(REMOVAL_DIR);
    }

    static List<String> getFilesForAddtion() {
        return plainFilenamesIn(ADDITION_DIR);
    }

    static List<String> getFilesForRemoval() {
        return plainFilenamesIn(REMOVAL_DIR);
    }


}
