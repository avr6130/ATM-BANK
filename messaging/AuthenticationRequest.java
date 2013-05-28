package messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:05 PM
 */
public class AuthenticationRequest extends Payload {

    private int pin = 0;

    public AuthenticationRequest(int pin, int accountNumber) {
        super(accountNumber);
        this.pin = pin;
    }

    public int getPin() {
        return this.pin;
    }
}
