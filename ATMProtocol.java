import java.io.*;

/**
 * An ATMProtocol processes local commands sent to the ATM and writes to or reads
 * from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class ATMProtocol implements Protocol {

    private PrintWriter writer;
    private BufferedReader reader;


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

    /* Interpret a command sent to the ATM and print the result to the output stream. */
    private void processCommand(String command) {

        if (command.toLowerCase().matches("begin-session")) {
            System.out.println("begin-session entered");

        } else if (command.toLowerCase().matches("balance")) {
            System.out.println("balance entered");

        } else if (command.toLowerCase().matches("deposit")) {
            System.out.println("deposit entered");

        } else if (command.toLowerCase().matches("withdraw")) {
            System.out.println("withdraw entered");

        } else if (command.toLowerCase().matches("end-session")) {
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
