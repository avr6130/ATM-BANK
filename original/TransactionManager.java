package original;

import messaging.BalanceRequest;
import messaging.BalanceResponse;
import messaging.SessionRequest;
import messaging.SessionResponse;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Group2
 * Date: 5/25/13
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */

public class TransactionManager {
    private boolean transactionActive = false;
    private AtmCardClass atmCard;
    private File cardFile;
    private int activeAccountNum = 0;
    private int numberOfFailedLoginAttempts;

    private SessionRequest sessionRequest;
    private SessionResponse sessionResponse;
    private BalanceRequest balanceRequest;
    private BalanceResponse balanceResponse;

    public TransactionManager() {
    }

    public boolean transactionActive() {
        return transactionActive;
    } // end transactionActive

    public SessionRequest requestSession(String[] splitCmdString) throws IOException {

        int enteredPin = 0;

        // Check if more than one argument was read from stdin
        if (!(splitCmdString.length > 1)) {

            // Not enough command line arguments were given so set the
            // transaction state to inactive and the session request to null
            transactionActive = false;
            sessionRequest = null;

        } // end if length < 1

        else { // splitCmdString.length IS greater than 1 so multiple args were given

            // Prepare and read the original.ATM card for the requested username
            cardFile = new File(splitCmdString[1] + ".card");

            // if the card doesn't exist with the given user name
            if (!cardFile.isFile()) {

                // User's name does not match the card or the file doesn't exist so set the
                // transaction state to inactive and the session request to null
                transactionActive = false;
                sessionRequest = null;

            } // end if not a valid card file

            else { // this IS a valid card file

                // Read the original.ATM card into a class variable
                atmCard = (AtmCardClass) Disk.load(splitCmdString[1] + ".card");

                // Get the pin
                System.out.print("Enter your PIN: ");
                enteredPin = cin.readInt();

                // Get the required information out of the card and prepare the message
                sessionRequest = new SessionRequest(enteredPin, atmCard.getAccountNumber());

            } // end else this IS a valid card file

        } // end else splitCmdString.length IS greater than 1 so multiple args were given

        // Return the message to be sent, or null
        return sessionRequest;

    } // end requestSession

    public void sessionResponse(SessionResponse sessionResponse) {

        try {
            // Set the transaction state true or false
            if (transactionActive = sessionResponse.isSessionValid() == true) {

                // Set the active active account number
                activeAccountNum = sessionResponse.getAccountNumber();

//                System.out.println(" User " + atmCard.getName() + " is Authorized");
                System.out.println(" User is Authorized");

            } // end if transactionActive
            else {

                System.out.println("Unauthorized");
            } // end else

        } catch (UnknownError e) {
            e.getMessage();
            e.printStackTrace();
        } // end catch
    } // end sessionResponse

    public boolean requestBalance(String[] splitCmdString) {

        boolean success = false;

        try {
            // Read the original.ATM card for username and account number
            readAtmCard(splitCmdString);

            if (atmCard == null) {
                success = false;
            } // end if atmCard == null
            else {
                // Send a balance request to the back
                balanceRequest = new BalanceRequest(atmCard.getAccountNumber());
                success = true;
            } // end else atmCard not null

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } // end catch
        return success;  //To change body of created methods use File | Settings | File Templates.

    }  // end requestBalance

    private void readAtmCard(String[] splitCmdString) throws IOException {

        // Prepare and read the original.ATM card for the requested username
        cardFile = new File(splitCmdString[1] + ".card");

        if (!cardFile.isFile()) {

            // Not a valid card file so set the card class variable to null.
            atmCard = null;

        } else { // this IS a valid card file

            // Load the contents of the stored card into the card class variable.
            atmCard = (AtmCardClass) Disk.load(splitCmdString[1] + ".card");
        }

    } // end readAtmCard

} // end class original.TransactionManager
