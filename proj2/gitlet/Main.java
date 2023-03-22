package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.exitWithMessage("Please enter a command.");
        }
        Repository.read();

        String command = args[0];
        int numOfOperands = args.length - 1;
        switch(command) {
            case "init":
                validateOperands(0, numOfOperands);
                Repository.initialize();
                break;
            case "add":
                validateRepository();
                validateOperands(1, numOfOperands);
                String filename = args[1];
                StagingArea.add(filename);
                break;
            case "commit":
                validateRepository();
                validateOperands(1, numOfOperands);
                String message = args[1];
                Repository.commit(message);
                break;
            case "rm":
                validateRepository();
                validateOperands(1, numOfOperands);
                filename = args[1];
                StagingArea.remove(filename);
                break;
            case "log":
                validateRepository();
                validateOperands(0, numOfOperands);
                Repository.log();
                break;
            case "global-log":
                validateRepository();
                validateOperands(0, numOfOperands);
                Repository.globalLog();
                break;
            case "find":
                validateRepository();
                validateOperands(1, numOfOperands);
                message = args[1];
                Repository.find(message);
                break;
            case "status":
                validateRepository();
                validateOperands(0, numOfOperands);
                Repository.status();
                break;
            case "checkout":
                validateRepository();
                Repository.checkout(args);
                break;
            case "branch":
                validateRepository();
                validateOperands(1, numOfOperands);
                String branch = args[1];
                Repository.branch(branch);
                break;
            case "rm-branch":
                validateRepository();
                validateOperands(1, numOfOperands);
                branch = args[1];
                Repository.remove(branch);
                break;
            case "reset":
                validateRepository();
                validateOperands(1, numOfOperands);
                String hash = args[1];
                Repository.reset(hash);
                break;
            case "merge":
                validateRepository();
                validateOperands(1, numOfOperands);
                branch = args[1];
                Repository.merge(branch);
                break;
            default:
                Utils.exitWithMessage("No command with that name exists.");
        }
    }

    private static void validateRepository() {
        if (!Repository.isInitialized()) {
            Utils.exitWithMessage("Not in an initialized Gitlet directory.");
        }
    }

    private static void validateOperands(int expected, int actual) {
        if (expected != actual) {
            Utils.exitWithMessage("Incorrect operands.");
        }
    }

}
