// Added to project

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: aricco
 * Date: 5/21/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */


public class AccountManager {

    // Just provide some initial values for account variables
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

} // class AccountManager
