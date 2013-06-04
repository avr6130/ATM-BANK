package original;

import java.io.*;
import java.net.Socket;

/**
 * The main class for the bank. The bank must constantly listen for
 * input from both the command line and the router.
 */

public class Bank {

    private final static String prompt = "Bank: ";
    private static AccountManager accountManager = new AccountManager();

    public static void main(String[] args) {
    	//Initialize properties
    	PropertiesFile.getProperties();
    	
    	//Initialize user accounts
        open(); 

        if (args.length != 1) {
            System.out.println("Usage: java Bank <Bank-port>");
            System.exit(1);
        }

        int bankPort = Integer.parseInt(args[0]);

        try {
            // Check to see if the file that contains the accounts exists.
            // If it doesn't then create the accounts and write them to the file.
            // The files used for ATM cards are also created within the AccountManager.
            if (Boolean.getBoolean(PropertiesFile.VM_PROP_BUILD_EXT_DATA)) {
                // Set up initial accounts with account names and balances
                accountManager.createAccount("Alice", 100.00);
                accountManager.createAccount("Bob", 100.00);
                accountManager.createAccount("Carol", 0.00);

                accountManager.storeAllAccounts();
            }
            else {
                accountManager.retrieveAllAccounts();
            }

            /* Connect to port */
            Socket socket = new Socket("localhost", bankPort);
            final BankProtocol bankProtocol = new BankProtocol(socket);

            /* Handle command-line input */
            Thread local = new Thread() {
                public void run() {
                    System.out.print(prompt);
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

                    try {
                        bankProtocol.processLocalCommands(stdIn, prompt);
                    } catch (IOException e) {
                        System.out.println("Failed to process user input.");
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            };

            /* Handle router input */
            Thread remote = new Thread() {
                public void run() {
                    try {
                        bankProtocol.processRemoteCommands(prompt);
                    } catch (IOException e) {
                        System.out.println("Failed to process remote input.");
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            };

            local.start();
            remote.start();

            try {
                local.join();
                remote.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            /* Clean up */
            try {
                bankProtocol.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close socket.");
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("Failed to connect to bank on port " + bankPort + ". Please try a different port.");
            System.exit(0);
        }
    }


    public static void processDep(int acctNo, double amt) {

    }


    public static int processWith(int acctNo, double amt) {
        return 0;
    }


    public static double processBal(int acctNo) {
        return 0.0;
    }


    public static int validate(int acct_no, int pin_no) {
        return 0;
    }


    public static void open() {

    }

}
