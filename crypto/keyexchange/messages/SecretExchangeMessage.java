package crypto.keyexchange.messages;

import javax.crypto.SealedObject;

public class SecretExchangeMessage extends KeyExchangeMessage {

	private SealedObject sealedPayload;
	private int sessionId;

	public SecretExchangeMessage(SealedObject sealedPayload, int sessionId) {
		super(KeyExchangeMessage.MessageType.SecretExchange);
		this.sealedPayload = sealedPayload;
		this.sessionId = sessionId;
	}

	public SealedObject getSealedPayload() {
		return sealedPayload;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}
}
