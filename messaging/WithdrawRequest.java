package messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:36 PM
 */
public class WithdrawRequest extends Payload {

    private double withdrawAmount = 0;

    public WithdrawRequest(int accountNumber, double withdrawAmount) {
        super(accountNumber);
        this.withdrawAmount = withdrawAmount;
    }

    public double getWithdrawAmount() {
        return this.withdrawAmount;
    }
}
