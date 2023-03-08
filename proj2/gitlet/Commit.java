package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;

import static gitlet.Repository.head;

public class Commit implements Serializable {

    private String hash;
    private String message;
    private String timestamp;
    private Commit parent;
    private HashMap<String, String> blobs;

    public Commit() {
//        this.hash = generateHash();
        this.message = "initial commit";
        this.timestamp = new Date(0L).toString();
        this.parent = null;
        this.blobs = new HashMap<>();
        setHash();
    }

    public Commit(String message) {
//        this.hash = generateHash();
        this.message = message;
        this.timestamp = new Date().toString();
        this.parent = head;
//        this.blobs = setBlobs();
//        this.setHash();
    }



    public void setBlobs(List<String> filesForAddition, List<String> filesForRemoval) {
        HashMap<String, String> blobs = Blob.copyBlobs(parent);
        System.out.println("files add:");
        for (String filename : filesForAddition) {
            if (!filesForRemoval.contains(filename)) {
                System.out.println(filename);
                Blob blob = new Blob(StagingArea.ADDITION_DIR, filename);
                blobs.put(filename, blob.hash());
                blob.save();
            }
        }
        System.out.println("file remove:");
        for (String filename : filesForRemoval) {
            System.out.println(filename);
            blobs.remove(filename);
        }
        this.blobs = blobs;
    }


    public void setHash() {
        this.hash = sha1(serialize(this));
    }

    public boolean containsBlob(String filename) {
        return blobs.containsKey(filename);
    }

    public Blob getBlob(String hash) {
        return Blob.readFromFile(hash);
    }

    @Override
    public String toString() {
        return String.format("===\ncommit %s\nDate: %s\n%s\n", hash, timestamp, message);
    }

    public void printBlobs() {
        Set<String> keys = blobs.keySet();
        System.out.println("blobs:");
        for (String key : keys) {
            System.out.println(key + " " + blobs.get(key));
        }
        System.out.println("-----");
//        System.out.println(parent);
    }

    public Commit parent() {
        return parent;
    }

    public HashMap<String, String> blobs() {
        return blobs;
    }
//    public void setBlobs(HashMap<String, Blob> blobs) {
//        this.blobs = blobs;
//    }

    public String hash() {
        return hash;
    }

    public static Commit readFromFile(String hash) {
        if (hash != null) {
            File f = join(CommitTree.COMMITS_DIR, hash);
            if (f.exists()) {
                return readObject(f, Commit.class);
            }
        }
        return null;
    }

    public void save() {
        File f = join(CommitTree.COMMITS_DIR, this.hash);
        writeObject(f,this);
    }

    public String message() {
        return message;
    }


}
