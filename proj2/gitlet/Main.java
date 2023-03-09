package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // What if args is empty?
        if (args.length == 0) {
            Utils.exitWithMessage("Please enter a command.");
        }

        Repository.readCommitTree();

        String command = args[0];
//        String filename = args[1];
        int numOfOperands = args.length - 1;
        switch(command) {
            case "init":
                // init` command
                validateOperands(0, numOfOperands);
                Repository.initialize();
                break;
            case "add":
                // `add [filename]` command
                validateRepository();
                validateOperands(1, numOfOperands);
                String filename = args[1];
//                System.out.println(filename);
                StagingArea.add(filename);
//                System.out.println("add");
                break;
            case "commit":
                // `commit [message]` command
                validateRepository();
                validateOperands(1, numOfOperands);

                String message = args[1];
                Repository.commit(message);
//                System.out.println("commit");
                break;
            case "rm":
                // `rm [filename]` command
                validateRepository();
                validateOperands(1, numOfOperands);

                filename = args[1];
//                System.out.println(filename);
                StagingArea.remove(filename);
//                System.out.println("rm");
                break;
            case "log":
                // `log` command
                validateRepository();
                validateOperands(0, numOfOperands);
//                System.out.println("log");
                Repository.log();
                break;
            case "global-log":
                validateRepository();
                validateOperands(0, numOfOperands);
//                System.out.println("log");
                Repository.globalLog();
                break;
            case "find":
                validateRepository();
                validateOperands(1, numOfOperands);

                message = args[1];
//                System.out.println("log");
//                Repository.globalLog();
                CommitTree.find(message);
                break;
            case "status":
                validateRepository();
                validateOperands(0, numOfOperands);
//                System.out.println("log");
                Repository.status();
                break;
            case "checkout":
                validateRepository();
//                validateOperands(1, numOfOperands);
//                System.out.println("log");
                Repository.checkout(args);

                break;
            case "branch":
                validateRepository();
                validateOperands(1, numOfOperands);

                String branch = args[1];
                Repository.branch(branch);
                break;
            case "viewfile":
                String hash = args[1];
                Blob b = Blob.readFromFile(hash);
                System.out.println(b.contents());
                break;
            default:
                Utils.exitWithMessage("No command with that name exists.");
        }
    }

    private static void validateRepository() {
        if (!Repository.isGitletInitialized()) {
            Utils.exitWithMessage("Not in an initialized Gitlet directory.");
        }
    }

    private static void validateOperands(int expected, int actual) {
        if (expected != actual) {
            Utils.exitWithMessage("Incorrect operands.");
        }
    }

}
