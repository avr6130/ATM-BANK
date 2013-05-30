package messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:19 PM
 */
public class AuthenticationResponse extends Payload {

    private boolean sessionValid = false;

    public AuthenticationResponse(int accountNumber, boolean sessionValid) {
        super(accountNumber);
        this.sessionValid = sessionValid;
    }

    public boolean isSessionValid() {
        return sessionValid;
    }
}