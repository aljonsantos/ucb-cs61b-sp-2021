package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Repository.tree;
import static gitlet.Utils.*;

import static gitlet.Repository.head;

public class Commit implements Serializable {
    public static final File COMMITS_DIR = join(Repository.GITLET_DIR, "commits");
    private String hash;
    private String message;
    private Date timestamp;
    private List<Commit> parents;
    private HashMap<String, String> blobs;

    public Commit(String message, Date date, Commit... parents) {
        this.message = message;
        this.timestamp = date;
        this.parents = Arrays.asList(parents);
        this.blobs = Blob.copyBlobs(this.parent());
    }

    public static Commit createIntialCommit() {
        Commit commit = new Commit("initial commit", new Date(0L), (Commit) null);
        commit.setHash();
        return commit;
    }

    public static Commit createCommit(String message) {
        Commit commit = new Commit(message, new Date(), head);
        commit.configBlobs();
        commit.setHash();
        return commit;
    }

    public static Commit createMergeCommit(String branch) {
        String message = String.format("Merged %s into %s.", branch, tree.branch());
        return new Commit(message, new Date(), head, tree.branches().get(branch));
//        return commit;
    }

    public String hash() {
        return this.hash;
    }

    public String message() {
        return this.message;
    }

    public List<Commit> parents() {
        return this.parents;
    }

    public Commit parent() {
        return parents.get(0);
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

    public void setHash() {
        this.hash = sha1(serialize(this));
    }

    public void setBlobs(HashMap<String, String> blobs) {
        this.blobs = blobs;
    }

    public boolean containsBlob(String filename) {
        return blobs.containsKey(filename);
    }


    public void configBlobs() {
        List<String> filesForAddition = StagingArea.filesForAddition();
        List<String> filesForRemoval = StagingArea.filesForRemoval();
        for (String filename : filesForAddition) {
            if (!filesForRemoval.contains(filename)) {
                Blob blob = new Blob(StagingArea.ADDITION_DIR, filename);
                blobs.put(filename, blob.hash());
                blob.save();
            }
        }
        for (String filename : filesForRemoval) {
            blobs.remove(filename);
        }
    }

    @Override
    public String toString() {
        if (parents.size() == 1) {
            return String.format("===\ncommit %s\nDate: %s\n%s\n", hash, formatDate(timestamp), message);
        }
        String parents = String.format("%s %s", this.parents.get(0).shortHash(), this.parents.get(1).shortHash());
        return String.format("===\ncommit %s\nMerge: %s\nDate: %s\n%s\n", hash, parents, formatDate(timestamp), message);
    }

    private String formatDate(Date date) {
        Formatter formatter = new Formatter();
        formatter.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", date);
        return formatter.toString();
    }

    private String shortHash() {
        return hash.substring(0, 7);
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Commit) {
            Commit other = (Commit) obj;
            return other.hash().equals(this.hash);
        }
        return false;
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
