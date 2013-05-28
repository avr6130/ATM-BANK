package original;

import messaging.Message;
import messaging.MessageHandler;
import messaging.SessionRequest;

import java.io.*;
import java.net.Socket;

/**
 * An ATMProtocol processes local splitStr[0]s sent to the ATM and writes to or reads
 * from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class ATMProtocol implements Protocol {

    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private TransactionManager atmTransactionManager = new TransactionManager();
    private SessionRequest sessionRequest;
    private MessageHandler messageHandler = new MessageHandler();

    public ATMProtocol(Socket socket) throws IOException {
        writer = new ObjectOutputStream(socket.getOutputStream());
        reader = new ObjectInputStream(socket.getInputStream());
    }

    /* Continue to read input until terminated. */
    public void processLocalCommands(BufferedReader stdIn, String prompt) throws IOException {
        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            processCommand(userInput);
            System.out.print(prompt);
        }

        stdIn.close();
    }

//    /* Process a remote command. */
//    private synchronized void processRemoteCommand(String command) {
//        boolean authenticated;
//
//        messageHandler.processMessage(command);
//
//    }

    /* Interpret a splitStr[0] sent to the ATM and print the result to the output stream. */
    private void processCommand(String command) throws IOException {

        // Split the input command on whitespace
        String[] splitCmdString = command.split("\\s+");
        boolean sessionIsAuthorized;
        Message msg = new Message();


        if (splitCmdString[0].toLowerCase().matches("begin-session")) {

            if (atmTransactionManager.transactionActive()) {
                System.out.println("Transaction currently in progress.  Please end-session before beginning a new session.");
            } // end if transactionActive()
            else {

                // Send a request to the bank to start a session
                sessionRequest = atmTransactionManager.requestSession(splitCmdString);
                if (sessionRequest == null)
                    System.out.println("Unauthorized");
                else {

                    msg.setPayload(sessionRequest);
                    writer.writeObject(msg);

                    processRemoteCommands();
                }

            } // end else transaction is not active

        } // end if begin-session request

        else if (splitCmdString[0].toLowerCase().matches("balance")) {
            if (!atmTransactionManager.transactionActive()) {
                System.out.println("Unauthorized");
            } // end if !transaction.isActive
            else {
                atmTransactionManager.requestBalance(splitCmdString);
                System.out.println("balance successfully requested");
            } // end else transaction is active

        } // end else if balance request

        else if (splitCmdString[0].toLowerCase().matches("withdraw")) {
            System.out.println("withdraw entered");

        } else if (splitCmdString[0].toLowerCase().matches("end-session")) {
            System.out.println("end-session entered");

        } else {
            System.out.println("Illegal input entered");
        } // end else

    } // end processCommand

   public void processRemoteCommands() throws IOException {
        Message msgObject;

        try {
            //while ((msgObject = (Message)reader.readObject()) != null) {
             msgObject = (Message) reader.readObject();
            messageHandler.processMessage(msgObject);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* Clean up all open streams. */
    public void close() throws IOException {
        writer.close();
        reader.close();
    }

}