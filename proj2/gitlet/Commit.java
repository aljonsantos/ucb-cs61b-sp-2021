package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

import static gitlet.Repository.head;

public class Commit implements Serializable {
    public static final File COMMITS_DIR = join(Repository.GITLET_DIR, "commits");
    private String hash;
    private String message;
    private Date timestamp;
    private Commit parent;
    private HashMap<String, String> blobs;

    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0L);
        this.parent = null;
        this.blobs = new HashMap<>();
        this.hash = generateHash();
    }

    public Commit(String message) {
        this.message = message;
        this.timestamp = new Date();
        this.parent = head;
        this.blobs = configureBlobs();
        this.hash = generateHash();
    }

    public String hash() {
        return this.hash;
    }

    public String message() {
        return this.message;
    }

    public Commit parent() {
        return this.parent;
    }

    public HashMap<String, String> blobs() {
        return this.blobs;
    }

    public List<String> blobFilenames() {
        return new ArrayList<>(blobs.keySet());
    }

    public List<String> blobHashes() {
        return new ArrayList<>(blobs.values());
    }

    public String blobHash(String filename) {
        return blobs.get(filename);
    }

    public String generateHash() {
        return sha1(serialize(this));
    }

    public boolean containsBlob(String filename) {
        return blobs.containsKey(filename);
    }

    public HashMap<String, String> configureBlobs() {
        HashMap<String, String> blobs = Blob.copyBlobs(parent);
//        System.out.println("files add:");
        for (String filename : StagingArea.filesForAddition) {
            if (!StagingArea.filesForRemoval.contains(filename)) {
//                System.out.println(filename);
                Blob blob = new Blob(StagingArea.ADDITION_DIR, filename);
                blobs.put(filename, blob.hash());
                blob.save();
            }
        }
//        System.out.println("file remove:");
        for (String filename : StagingArea.filesForRemoval) {
//            System.out.println(filename);
            blobs.remove(filename);
        }
        return blobs;
    }

    @Override
    public String toString() {
        return String.format("===\ncommit %s\nDate: %s\n%s\n", hash, formatDate(timestamp), message);
    }

    private String formatDate(Date date) {
        Formatter formatter = new Formatter();
        formatter.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", date);
        return formatter.toString();
    }

    public static Commit read(String hash) {
        List<String> lst = CommitTree.getAllCommits();

        int n = 2;
        List<String> matches = findStartsWith(hash.substring(0, n), lst);
        while (matches.size() > 1 && n <= hash.length()) {
            matches = findStartsWith(hash.substring(0, n), matches);
            n += 2;
        }

        if (matches.isEmpty()) {
            return null;
        }
        return readObject(join(COMMITS_DIR, matches.get(0)), Commit.class);
    }

    private static List<String> findStartsWith(String hash, List<String> lst) {
        List<String> matches = new ArrayList<>();
        for (String file : lst) {
            if (file.startsWith(hash)) {
                matches.add(file);
            }
        }
        return matches;
    }


    public void save() {
        writeObject(join(COMMITS_DIR, this.hash),this);
    }

    public void printBlobs() {
        Set<String> keys = blobs.keySet();
        System.out.println("blobs:");
        for (String key : keys) {
            System.out.println(key + " " + blobs.get(key));
        }
        System.out.println("-----");
    }

}
