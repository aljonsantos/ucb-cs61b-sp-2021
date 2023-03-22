package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Utils.*;

import static gitlet.Repository.head;

public class StagingArea {
    public static final File STAGING_DIR = join(Repository.GITLET_DIR, "staging");
    public static final File ADDITION_DIR = join(STAGING_DIR, "addition");
    public static final File REMOVAL_DIR = join(STAGING_DIR, "removal");

    public static List<String> filesForAddition() {
        return plainFilenamesIn(ADDITION_DIR);
    }

    public static List<String> filesForRemoval() {
        return plainFilenamesIn(REMOVAL_DIR);
    }

    public static void add(String filename) {
        File f = join(Repository.CWD, filename);
        if (!f.exists()) {
            exitWithMessage("File does not exist.");
        }

        boolean isFileIdenticalToHeads = new Blob(f).hash().equals(head.blobs().get(filename));
        if (!isFileIdenticalToHeads) {
//            System.out.println("added: " + filename);
            stageFile(filename);
        }
        else {
            unstageFile(filename);
        }
        unstageFileForRemoval(filename);
    }

    public static void remove(String filename) {
        if (!StagingArea.isStaged(filename) && !head.containsBlob(filename)) {
            exitWithMessage("No reason to remove the file.");
        }
        else if (head.containsBlob(filename)) {
            stageFileForRemoval(filename);
            deleteFile(Repository.CWD, filename);
        }
        unstageFile(filename);
    }

    public static void stageFile(String filename) {
        File f = join(Repository.CWD, filename);
        File copy = join(ADDITION_DIR, filename);
        writeContents(copy, readContents(f));
    }

    public static void unstageFile(String filename) {
        deleteFile(ADDITION_DIR, filename);
    }

    public static void stageFileForRemoval(String filename) {
        File f = join(Repository.CWD, filename);
        byte[] contents;
        if (f.exists()) {
            contents = readContents(f);
        }
        else {
            contents = Blob.read(head.blobs().get(filename)).contents();
        }
        File copy = join(REMOVAL_DIR, filename);
        writeContents(copy, contents);
    }

    public static void unstageFileForRemoval(String filename) {
        deleteFile(REMOVAL_DIR, filename);
    }

    public static boolean isStaged(String filename) {
        return join(ADDITION_DIR, filename).exists();
    }

    public static void clear() {
        deleteFilesIn(ADDITION_DIR);
        deleteFilesIn(REMOVAL_DIR);
    }

}
