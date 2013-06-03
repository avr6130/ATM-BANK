package crypto.keyexchange.messages;

public class SecretExchangeMessage extends KeyExchangeMessage {

	private byte[] secret;
	private int sessionId;

	public SecretExchangeMessage(byte[] secret, int sessionId) {
		super(KeyExchangeMessage.MessageType.SecretExchange);
		this.secret = secret;
		this.sessionId = sessionId;
	}

	public byte[] getSecret() {
		return secret;
	}
	
	public int getSessionId() {
		return this.sessionId;
	}
}
