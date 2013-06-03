package crypto.keyexchange.messages;

public abstract class KeyExchangeMessage {

	public static enum MessageType {
		InitiateExchange(), CertificateResponse(), SecretExchange();
	}
	
	public final MessageType mType;
	
	public KeyExchangeMessage(MessageType mType) {
		this.mType = mType;
	}

	public MessageType getmType() {
		return mType;
	}

}
