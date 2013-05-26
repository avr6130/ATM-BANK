package messaging;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Group2
 * Date: 5/25/13
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionManager {
    private boolean active = false;
    private short numberOfFailedLoginAttempts = 0;
    private final short MAXFAILEDLOGINATTEMPTS = 3;
    private AtmCardClass atmCard;
    private File cardFile;

    public TransactionManager() {
    }

    public boolean transactionActive() {
        return active;
    } // end transactionActive

    public boolean requestSession(String[] splitStr) {

        int enteredPin;

            //if (!transactionManager.transactionActive()) {

                //if (splitCmdString.length > 1) {
        // Prepare and read the ATM card for the requested username
        cardFile = new File(splitStr[1] + ".card");
        if (cardFile.isFile()) {
            atmCard = (AtmCardClass) Disk.load(splitStr[1] + ".card");

            System.out.print("Enter your PIN: ");
            System.out.println();
            enteredPin = cin.readInt();

            System.out.println("begin-session for " + atmCard.getName() + atmCard.getAccountNumber());
        }

    public boolean loginLockedOut() {
        if (numberOfFailedLoginAttempts < MAXFAILEDLOGINATTEMPTS) {
            return false;
        } // end if
        else {
            return true;
        } // end else

    } // end loginLockedOut

}
