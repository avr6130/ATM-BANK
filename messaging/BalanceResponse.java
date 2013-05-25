package messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:29 PM
 */
public class BalanceResponse extends Payload {

    private int balance = 0;

    public BalanceResponse(int accountNumber, int balance) {
        super(accountNumber);
        this.balance = balance;
    }

    public int getBalance() {
        return this.balance;
    }
}
