package com.group2.crypto.keyexchange.messages;

public class InitiationMessage extends KeyExchangeMessage {

	public InitiationMessage() {
		super(KeyExchangeMessage.MessageType.InitiateExchange);
	}
}
