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
    private final short MAX_FAILED_ATTEMPTS = 3;
    private final int NUM_OF_SECS = 60;
    private boolean transactionActive = false;
    private short numberOfFailedLoginAttempts = 0;
    private AtmCardClass atmCard;
    private File cardFile;
    private SessionRequest sessionRequest;
    private SessionResponse sessionResponse;

    public TransactionManager() {
    }

    public boolean transactionActive() {
        return transactionActive;
    } // end transactionActive

    public boolean requestSession(String[] splitCmdString) throws IOException {

        int enteredPin;

        // Check if more than one argument was read from stdin
        if (!(splitCmdString.length > 1)) {

            // Tell the caller the request was denied
            transactionActive = false;

        } // end if length < 1

        else { // splitCmdString.length IS greater than 1 so multiple args were given

            // Prepare and read the ATM card for the requested username
            cardFile = new File(splitCmdString[1] + ".card");

            if (!cardFile.isFile()) {

                // Tell the caller the request was denied
                transactionActive = false;

            } // end if not a card file

            else { // this IS a valid card file
                atmCard = (AtmCardClass) Disk.load(splitCmdString[1] + ".card");

                if (atmCard.getNextValidLoginTime() > System.currentTimeMillis()) {
                    long time1 = System.currentTimeMillis();
                    System.out.println("This account is currently locked out.  Try again later.");

                    // Tell the caller the request was denied
                    transactionActive = false;

                } // if locked out
                else { // not locked out

                    System.out.print("Enter your PIN: ");
                    enteredPin = cin.readInt();

                    sessionRequest = new SessionRequest(enteredPin, atmCard.getAccountNumber());

                    // TODO
                    // send the sessionRequest message to the bank
                    // Debug Debug Debug  Debug  Debug  Debug  Debug  Debug  Debug  Debug
                    // the next line should use if (sessionResponse.isSessionValid()   )
                    //if (enteredPin != sessionRequest.getPin())
                    if (enteredPin != 3) {

                        numberOfFailedLoginAttempts++;

                        if (numberOfFailedLoginAttempts == MAX_FAILED_ATTEMPTS) {

                            // Set a lockout time on the ATM card
                            atmCard.setNextValidLoginTime((NUM_OF_SECS * 1000L) + System.currentTimeMillis());

                            // Rewrite the ATM card with a lockout time in the future
                            Disk.save(atmCard, atmCard.getName() + ".card");

                            System.out.println("Maximum login attempts exceeded.  Login for this account is temporarily disabled");

                            transactionActive = false;

                        } // end if MAX_FAILED_ATTEMPTS

                    } // This comment should change after debugging end if enteredPin != getPin()
                    else { // entered pin matches stored pin
                        transactionActive = true;
                    } // end else entered pin matches stored pin
                } // else not locked out
            } // end else this IS a valid card file
        } // end else splitCmdString.length IS greater than 1 so multiple args were given

        // Tell the caller the result of the request
        return transactionActive;

    } // end requestSession

    public boolean loginLockedOut() {
        if (numberOfFailedLoginAttempts < MAX_FAILED_ATTEMPTS) {
            return false;
        } // end if
        else {
            return true;
        } // end else

    } // end loginLockedOut

} // end class TransactionManager
