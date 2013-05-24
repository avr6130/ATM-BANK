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
    private static int accountNumber     = 1654192;
    private static int numberOfAccounts  = 0;
    private static int pin               = 1740;
    private static Account[] accts       = new Account[MAX_ACCOUNTS];
    private static Disk fileIO;

    public void createAccount(String customerName, double initialBalance) {

        accts[numberOfAccounts] = new Account(customerName, accountNumber, pin, initialBalance);

        // Debug
        accts[numberOfAccounts].print();

        // Manipulate account variables
        numberOfAccounts++;
        accountNumber++;
        pin += 2578;

    } // end createAccount

    public void printAllAccounts() {
        for (int i=0; i<MAX_ACCOUNTS; i++) {
            accts[i].print();
        } // end for
    } // end printAllAccounts

    public void retrieveAccounts() throws IOException {
        accts = (Account[]) fileIO.load("accountsFile.objs");
    } // end retrieveAccounts

    public void storeAccounts() throws IOException {
       fileIO.save(accts, "accountsFile.objs");
    } // end storeAccounts


}
