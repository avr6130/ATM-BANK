package original;

import messaging.*;

import java.io.*;
import java.net.Socket;

/**
 * A BankProtocol processes local and remote commands sent to the Bank and writes to
 * or reads from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class BankProtocol implements Protocol {

    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    // ########### temporary #### delete me ######################
    private int tempAcctNumber = 0;

    public BankProtocol(Socket socket) throws IOException {

        writer = new ObjectOutputStream(socket.getOutputStream());
        reader = new ObjectInputStream(socket.getInputStream());
    }

    /* Process commands sent through the router. */
    public void processRemoteCommands() throws IOException {
        Message msgObject;

        try {
            while ((msgObject = (Message) reader.readObject()) != null) {
                processRemoteCommand(msgObject);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* Process commands from the command line. */
    public void processLocalCommands(BufferedReader stdIn, String prompt) throws IOException {
        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            processLocalCommand(userInput);
            System.out.print(prompt);
        }

        stdIn.close();
    }

    /* Process a remote command. */
    //private synchronized void processRemoteCommand(String command) {
    private synchronized void processRemoteCommand(Message messageObject) {
        boolean authenticated = false;
        Message msg = new Message();

        //authenticated = messageHandler.processMessage(messageObject);

        // ########### temporary #####################################
        // This is temporary but useful for initial testing of message exchange.
        // The functionality can probably be used almost as is, but in the proper place.
        // Now send the session response back to the ATM
        try {

            Payload payload = messageObject.getPayload();
            if (payload instanceof SessionRequest) {
                AccountManager accountManager = new AccountManager();
                authenticated = accountManager.validateSessionRequest((SessionRequest) payload);
                tempAcctNumber = payload.getAccountNumber();

                // Create the SessionResponse object and give it the account number and result of PIN validation.
                SessionResponse sessionResponse = new SessionResponse(messageObject.getPayload().getAccountNumber(), authenticated);

                // Set the message payload to the sessionResponse object
                msg.setPayload(sessionResponse);

                // Send the message back to the ATM
                writer.writeObject(msg);

            } // end SessionRequest

            // This is completely fake, but is here to test that basic balance response is
            // handled properly on the ATM side.
            else if (payload instanceof BalanceRequest) {
                BalanceResponse balanceResponse = new BalanceResponse(tempAcctNumber, 13);
                msg.setPayload(balanceResponse);
                writer.writeObject(msg);

            } // end if BalanceResponse

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        } // end catch
        // ########### end temporary section #####################################

    } // end processRemoteCommand

    /* Process user input. */
    private synchronized void processLocalCommand(String command) {

        if (command.toLowerCase().matches("balance")) {
            System.out.println("balance entered");

        } else if (command.toLowerCase().matches("deposit")) {
            System.out.println("deposit entered");

        } else if (command.toLowerCase().matches("withdraw")) {
            System.out.println("withdraw entered");

        } else if (command.toLowerCase().matches("validate")) {
            System.out.println("validate entered");

        } else {
            System.out.println("Illegal input entered");
        } // end else
    }

    /* Clean up all open streams. */
    public void close() throws IOException {
        reader.close();
        writer.close();
    }
}
