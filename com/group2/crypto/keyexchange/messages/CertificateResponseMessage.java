package com.group2.crypto.keyexchange.messages;

import java.io.Serializable;
import java.security.SignedObject;

public class CertificateResponseMessage extends KeyExchangeMessage implements Serializable {

	private SignedObject bankCert;
	private int sessionId;

	public CertificateResponseMessage(SignedObject bankCert, int sessionId) {
		super(KeyExchangeMessage.MessageType.CertificateResponse);
		this.bankCert = bankCert;
		this.sessionId = sessionId;
	}

	public SignedObject getBankCert() {
		return bankCert;
	}

	public int getSessionId() {
		return sessionId;
	}
}
