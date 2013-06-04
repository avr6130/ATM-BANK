package crypto.keyexchange.messages;

import java.io.Serializable;

public abstract class KeyExchangeMessage implements Serializable {

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
