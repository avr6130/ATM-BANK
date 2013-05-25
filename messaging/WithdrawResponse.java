package messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:40 PM
 */
public class WithdrawResponse extends Payload {

    private int withdrawAmount = 0;

    public WithdrawResponse(int accountNumber, int withdrawAmount) {
        super(accountNumber);
        this.withdrawAmount = withdrawAmount;
    }

    public int getWithdrawAmount() {
        return this.withdrawAmount;
    }
}
