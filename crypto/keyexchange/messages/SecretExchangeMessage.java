package crypto.keyexchange.messages;

import javax.crypto.SealedObject;

public class SecretExchangeMessage extends KeyExchangeMessage {

	private SealedObject so;
	private int sessionId;

	public SecretExchangeMessage(SealedObject so, int sessionId) {
		super(KeyExchangeMessage.MessageType.SecretExchange);
		this.so = so;
		this.sessionId = sessionId;
	}

	public SealedObject getSecret() {
		return so;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}
}
