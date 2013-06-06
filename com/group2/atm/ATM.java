package com.group2.atm;

import java.io.*;
import java.net.Socket;

import com.group2.util.PropertiesFile;
import com.group2.util.Protocol;


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
            Protocol atmProtocol = new ATMProtocol(socket);

            /* Handle command-line input */
            System.out.print(prompt);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            try {
                atmProtocol.processLocalCommands(stdIn, prompt);
            } catch (IOException e) {
                System.out.println("Failed to process local input.");
                System.exit(0);
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