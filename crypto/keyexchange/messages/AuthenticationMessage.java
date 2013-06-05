package crypto.keyexchange.messages;


/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:19 PM
 */
public class AuthenticationMessage extends KeyExchangeMessage {

    private boolean sessionValid = false;

    public AuthenticationMessage(int accountNumber, boolean sessionValid) {
    	super(KeyExchangeMessage.MessageType.AuthenticationResponse);
        this.sessionValid = sessionValid;
    }

    public boolean isSessionValid() {
        return sessionValid;
    }
}
