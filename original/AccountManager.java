package original;

import messaging.SessionRequest;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Group2
 * Date: 5/21/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */


public class AccountManager {

    private final short MAX_FAILED_ATTEMPTS = 3;
    private final int NUM_OF_LOCKOUT_SECONDS = 60;
    private static final int MAX_ACCOUNTS = 3;
    private static int accountNumber = 16849327;
    private static int numberOfAccounts = 0;
    private static int initialPin = 1095;
    private static Account[] accts = new Account[MAX_ACCOUNTS];
    private static Disk fileIO;

    public void createAccount(String customerName, double initialBalance) throws IOException {

        try {
            // Create the account, including an ATM card internal to constructor.
            accts[numberOfAccounts] = new Account(customerName, accountNumber, initialPin, initialBalance);

            createAtmCardFile(accts[numberOfAccounts].getAtmCard());

            // Debug
            accts[numberOfAccounts].print();

            // Manipulate account variables
            numberOfAccounts++;
            accountNumber++;
            initialPin += 2578;

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        }

    } // end createAccount

    public void printAllAccounts() {
        for (int i = 0; i < MAX_ACCOUNTS; i++) {
            accts[i].print();
        } // end for
    } // end printAllAccounts

    public void retrieveAllAccounts() throws IOException {
        accts = (Account[]) Disk.load("accountsFile");
    } // end retrieveAllAccounts

    public void storeAllAccounts() throws IOException {
        Disk.save(accts, "accountsFile");
    } // end storeAllAccounts

    public void createAtmCardFile(AtmCardClass atmCard) throws IOException {
        Disk.save(atmCard, atmCard.getName() + ".card");
    } // end createAtmCardFile

    public AtmCardClass retrieveAtmCard(String name) throws IOException {
        return (AtmCardClass) Disk.load(name + ".card");
    } // end retrieveCard

    public boolean validateSessionRequest(SessionRequest msg) {

        int curAcct = 0;
        boolean authenticated = false;

        // This should be a hash table but I'm trying to get the basics of the
        // assignment down, and because there are only 3 elements this is easier for now...
        for (curAcct = 0; curAcct < MAX_ACCOUNTS; curAcct++) {
            if (msg.getAccountNumber() == accts[curAcct].getID()) {
                break;
            } // end if entered account number is valid
        } // end for

        if (accts[curAcct].getNextValidLoginTime() > System.currentTimeMillis()) {

            System.out.println("This account is currently locked out.  Try again later.");

            // Fail authentication/validation and return immediately
            return authenticated = false;

        } // end if getNextValidationTime

        if (accts[curAcct].getCurrentNumOfFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {

            System.out.println("MAX_FAILED_LOGIN_ATTEMPTS.");

            // Set the valid login time into the future to avoid repetitive false login attempts
            accts[curAcct].setNextValidLoginTime((NUM_OF_LOCKOUT_SECONDS * 1000L) + System.currentTimeMillis());

            // Now that a lockout time has been set, reset the number of failed login attempts
            accts[curAcct].resetCurrentNumOfFailedLoginAttempts();

            return authenticated = false;
        } // end if now MAX_FAILED_ATTEMPTS

        // Check the pin
        if (msg.getPin() != accts[curAcct].getPin()) {
            System.out.println("PIN didn't match.");
            accts[curAcct].incrementCurrentNumOfFailedLoginAttempts();

            return authenticated = false;

        } // end if entered pin != pin
        else { // The pin entered must have been good
            System.out.println("AUTHENTICATED!!.");
            accts[curAcct].resetCurrentNumOfFailedLoginAttempts();
            return authenticated = true;
        } // end else -> entered pin is correct
    } // end validateSessionRequest()
} // class AccountManager
