import messaging.TransactionManager;

import java.io.*;

/**
 * An ATMProtocol processes local splitStr[0]s sent to the ATM and writes to or reads
 * from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class ATMProtocol implements Protocol {

    private PrintWriter writer;
    private BufferedReader reader;
    private TransactionManager transactionManager;

    public ATMProtocol(InputStream inputStream, OutputStream outputStream) {
        writer = new PrintWriter(outputStream, true);
        reader = new BufferedReader(new InputStreamReader(inputStream));
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

    /* Interpret a splitStr[0] sent to the ATM and print the result to the output stream. */
    private void processCommand(String command) throws IOException {

        // Split the input command on whitespace
        String[] splitCmdString = command.split("\\s+");

        if (splitCmdString[0].toLowerCase().matches("begin-session")) {

            transactionManager.requestSession(splitCmdString);

        } // end begin-session
        else if (splitCmdString[0].toLowerCase().matches("balance")) {
            System.out.println("balance entered");

        } else if (splitCmdString[0].toLowerCase().matches("withdraw")) {
            System.out.println("withdraw entered");

        } else if (splitCmdString[0].toLowerCase().matches("end-session")) {
            System.out.println("end-session entered");

        } else {
            System.out.println("Illegal input entered");
        } // end else

    } // end processCommand

    /* Clean up all open streams. */

    public void close() throws IOException {
        writer.close();
        reader.close();
    }

}
