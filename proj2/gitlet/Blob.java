package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Utils.*;
public class Blob implements Serializable {

    public static final File BLOBS_DIR = join(Repository.GITLET_DIR, "blobs");

    private String hash;
    private String filename;
    private byte[] contents;

    public Blob(File f) {
        this.filename = f.getName();
        this.contents = readContents(f);
        this.hash = sha1(this.contents);
    }

    public Blob(File dir, String filename) {
        this(join(dir, filename));
    }

    static HashMap<String, String> copyBlobs(Commit from) {
        HashMap<String, String> blobs = from.blobs();
        HashMap<String, String> copy = new HashMap<>();
        if (blobs != null) {
            copy.putAll(blobs);
        }
        return copy;
    }


    public static Blob readFromFile(String hash) {
        if (hash != null) {
            File f = join(BLOBS_DIR, hash);
            if (f.exists()) {
                return readObject(f, Blob.class);
            }
        }
        return null;
    }

    public void save() {
        File f = join(BLOBS_DIR, this.hash);
        writeObject(f,this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Blob) {
            Blob other = (Blob) obj;
            return this.hash.equals(other.hash);
        }
        return false;
    }

    public String hash() {
        return hash;
    }


    public String contents() {
        return new String(contents);
    }


}
