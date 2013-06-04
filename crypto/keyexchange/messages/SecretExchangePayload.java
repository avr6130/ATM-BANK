package crypto.keyexchange.messages;

import java.io.Serializable;
import java.security.Key;

public class SecretExchangePayload implements Serializable {

	private int sessionId;
	private int accountNumber;
	private Key sessionKey;
	
	public SecretExchangePayload(int accountNumber, int sessionId, Key sessionKey) {
		this.accountNumber = accountNumber;
		this.sessionId = sessionId;
		this.sessionKey = sessionKey;
	}

	public int getSessionId() {
		return sessionId;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public Key getSessionKey() {
		return sessionKey;
	}

}
