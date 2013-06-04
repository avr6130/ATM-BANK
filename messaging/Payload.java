package messaging;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:01 PM
 */
public abstract class Payload implements Serializable {

    private int accountNumber = 0;

    public Payload(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

	@Override
	public String toString() {
		return "Payload [accountNumber=" + accountNumber + "]";
	}

}
