package com.group2.crypto.keyexchange.messages;

public class AuthenticationMessage extends KeyExchangeMessage {

    private boolean sessionValid = false;

    /**
     * Constructs a message which authenticates the session and pin
     * sent from the client (ATM).
     * @param accountNumber number as
     * @param sessionValid
     */
    public AuthenticationMessage(int accountNumber, boolean sessionValid) {
    	super(KeyExchangeMessage.MessageType.AuthenticationResponse);
        this.sessionValid = sessionValid;
    }

    public boolean isSessionValid() {
        return sessionValid;
    }
}
