package original;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: aricco
 * Date: 5/25/13
 * Time: 1:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class AtmCardClass implements Serializable {

    private String customerName;
    private int accountNumber;

    // Default constructor used for reading in the card file on disk
    public AtmCardClass() {}

    public AtmCardClass(String name, int accountNumber) {
        this.customerName = name;
        this.accountNumber = accountNumber;
    } // end original.AtmCardClass constructor

    public String getName() {
        return customerName;
    } // end getName

    public int getAccountNumber() {
        return accountNumber;
    } // end getAccountNumber

} // end original.AtmCardClass