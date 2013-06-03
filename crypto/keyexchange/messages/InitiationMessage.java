package crypto.keyexchange.messages;

public class InitiationMessage extends KeyExchangeMessage {

	public InitiationMessage() {
		super(KeyExchangeMessage.MessageType.InitiateExchange);
	}
}
