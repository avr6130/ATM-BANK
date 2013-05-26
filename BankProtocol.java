import messaging.MessageHandler;

import java.io.*;

/**
 * A BankProtocol processes local and remote commands sent to the Bank and writes to
 * or reads from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class BankProtocol implements Protocol {

    private PrintWriter writer;
    private BufferedReader reader;
    private MessageHandler messageHandler;
    private AccountManager accountManager = new AccountManager();

    public BankProtocol(InputStream inputStream, OutputStream outputStream) {
        writer = new PrintWriter(outputStream, true);
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    /* Process commands sent through the router. */
    public void processRemoteCommands() throws IOException {
        String input;

        while ((input = reader.readLine()) != null) {
            System.out.println("After while Inside Bank processRemoteCommandssss");
            processRemoteCommand(input);
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

    /* Process a remote command and write out the result. */
    private synchronized void processRemoteCommand(String command) {
        boolean authenticated;
        System.out.println("Inside Bank processRemoteCommand");

        // Split the input command on whitespace
        String[] splitCmdString = command.split("\\s+");

        // messageHandler.processMessage();
        authenticated = accountManager.validateSessionRequest(splitCmdString);

        if (authenticated)
            System.out.println("Authenticated.");
        else
            System.out.println("Failed to authenticate.");

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
