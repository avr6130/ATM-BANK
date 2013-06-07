package com.group2.atm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.group2.util.PropertiesFile;


/**
 * The main class for the ATM.
 */

public class ATM {

    private static final String prompt = "ATM: ";

    public static void main(String[] args) {
        // Read port from properties
        int atmPort = Integer.parseInt(PropertiesFile.getProperty(PropertiesFile.PORT_ATM, "34002"));

        try {
            /* Connect to port */
            Socket socket = new Socket("localhost", atmPort);
            final ATMProtocol atmProtocol = new ATMProtocol(socket);

            /* Handle command-line input */
            Thread local = new Thread() {
                public void run() {
                    System.out.print(prompt);
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

                    try {
                        atmProtocol.processLocalCommands(stdIn, prompt);
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
                    	atmProtocol.processRemoteCommands();
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
                atmProtocol.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close socket.");
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("Could not connect to ATM on port " + atmPort + ". Please try a different port.");
            System.exit(0);
        }
    }

}