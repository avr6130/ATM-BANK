package com.group2.bank;

import java.io.*;
import java.net.Socket;

import com.group2.util.PropertiesFile;


/**
 * The main class for the bank. The bank must constantly listen for
 * input from both the command line and the router.
 */

public class Bank {

    private final static String prompt = "Bank: ";
    private static AccountManager accountManager = new AccountManager();

    public static void main(String[] args) {
    	// Read port from properties
        int bankPort = Integer.parseInt(PropertiesFile.getProperty(PropertiesFile.PORT_BANK, "34001"));

        try {
        	// Load the accounts into the system
            accountManager.loadAccounts();

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
                        bankProtocol.processRemoteCommands();
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
}
