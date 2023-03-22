package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;
public class Blob implements Serializable {
    public static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs");
    private String hash;
    private byte[] contents;

    public Blob(File f) {
        this.contents = readContents(f);
        this.hash = sha1(this.contents);
    }

    public Blob(File dir, String filename) {
        this(join(dir, filename));
    }

    public String hash() {
        return hash;
    }

    public byte[] contents() {
        return contents;
    }

    public static HashMap<String, String> copyBlobs(Commit commit) {
        if (commit != null) {
            HashMap<String, String> blobs = commit.blobs();
            HashMap<String, String> copy = new HashMap<>();
            if (blobs != null) {
                copy.putAll(blobs);
            }
            return copy;
        }
        return new HashMap<>();
    }

    public static void checkout(Commit commit, String filename) {
        String hash = commit.blobs().get(filename);
        Blob blob = read(hash);
        writeContents(join(Repository.CWD, filename), blob.contents());
    }

    public static boolean isModified(String file, Commit c1, Commit c2) {
        if (!c1.containsBlob(file) && !c2.containsBlob(file)) {
            return false;
        }
        if (!c1.containsBlob(file) || !c2.containsBlob(file)) {
            return true;
        }
        return !c1.blobHash(file).equals(c2.blobHash(file));
    }

    public static Blob read(String hash) {
        return readObject(join(BLOBS_DIR, hash), Blob.class);
    }

    public void save() {
        File f = join(BLOBS_DIR, this.hash);
        writeObject(f,this);
    }

}
