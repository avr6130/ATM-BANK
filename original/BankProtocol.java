package original;

import messaging.Message;
import messaging.MessageHandler;
import messaging.Payload;
import messaging.SessionRequest;

import java.io.*;
import java.net.Socket;

/**
 * A BankProtocol processes local and remote commands sent to the Bank and writes to
 * or reads from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class BankProtocol implements Protocol {

    //private PrintWriter writer;
    //private BufferedReader reader;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private MessageHandler messageHandler = new MessageHandler();
    private AccountManager accountManager = new AccountManager();

    // public BankProtocol(InputStream inputStream, OutputStream outputStream) {
    //public BankProtocol(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException {
    public BankProtocol(Socket socket) throws IOException {

        //writer = new PrintWriter(objectOutputStream, true);
        //reader = new BufferedReader(new InputStreamReader(objectInputStream));
        writer = new ObjectOutputStream(socket.getOutputStream());
        reader = new ObjectInputStream(socket.getInputStream());
    }

    /* Process commands sent through the router. */
    public void processRemoteCommands() throws IOException {
        String input;
        // SessionRequest msgObject = new SessionRequest(1,2);
        Message msgObject;

        //while ((input = reader.readLine()) != null) {
        try {
            while ((msgObject = (Message)reader.readObject()) != null) {
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
        boolean authenticated;

        messageHandler.processMessage(messageObject);

    }

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
