package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
//        this.hash = generateHash();
        this.message = "initial commit";
        this.timestamp = new Date(0L);
        this.parent = null;
        this.blobs = new HashMap<>();
        setHash();
    }

    public Commit(String message) {
//        this.hash = generateHash();
        this.message = message;
        this.timestamp = new Date();
        this.parent = head;
//        this.blobs = setBlobs();
//        this.setHash();
    }

    public void setBlobs(List<String> filesForAddition, List<String> filesForRemoval) {
        HashMap<String, String> blobs = Blob.copyBlobs(parent);
//        System.out.println("files add:");
        for (String filename : filesForAddition) {
            if (!filesForRemoval.contains(filename)) {
//                System.out.println(filename);
                Blob blob = new Blob(StagingArea.ADDITION_DIR, filename);
                blobs.put(filename, blob.hash());
                blob.save();
            }
        }
//        System.out.println("file remove:");
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

//    public Blob readBlob(String hash) {
//        return Blob.readFromFile(hash);
//    }

    @Override
    public String toString() {
        return String.format("===\ncommit %s\nDate: %s\n%s\n", hash, format(timestamp), message);
    }

    private String format(Date date) {
        Formatter formatter = new Formatter();
        formatter.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", date);
        return formatter.toString();
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


    public String hash() {
        return hash;
    }

    // save object and read object make it general (for commit and blob)

    public static Commit readFromFile(String hash) {
        if (hash != null) {
            List<String> files = plainFilenamesIn(COMMITS_DIR);
            if (files != null) {
                for (String file : files) {
                    if (file.startsWith(hash)) {
                        return readObject(join(COMMITS_DIR, file), Commit.class);
                    }
                }
            }

//            File f = join(CommitTree.COMMITS_DIR, hash);
//            if (f.exists()) {
//                return readObject(f, Commit.class);
//            }

//            File dir = join(Repository.OBJECTS_DIR, hash.substring(0, 2));
//            List<String> files = plainFilenamesIn(dir);
//            if (files != null) {
//                for (String file : files) {
//                    if (file.startsWith(hash.substring(2))) {
//                        return readObject(join(dir, file), Commit.class);
//                    }
//                }
//            }
        }
        return null;
    }

    public void save() {
        File f = join(COMMITS_DIR, this.hash);
        writeObject(f,this);
//        File dir = join(Repository.OBJECTS_DIR, hash.substring(0, 2));
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        writeObject(join(dir, hash.substring(2)), this);
    }

    public String message() {
        return message;
    }


}
