package crypto.keyexchange.messages;

public class SecretExchangeMessage extends KeyExchangeMessage {

	private byte[] secret;

	public SecretExchangeMessage(byte[] secret) {
		super(KeyExchangeMessage.MessageType.SecretExchange);
		this.secret = secret;
	}

	public byte[] getSecret() {
		return secret;
	}
}
