package com.group2.crypto.keyexchange.messages;

import java.io.Serializable;
import java.security.SignedObject;

import com.group2.authority.G2Constants;

public class CertificateResponseMessage extends KeyExchangeMessage implements Serializable {

	private SignedObject bankCert;
	private int sequenceId;

	public CertificateResponseMessage(SignedObject bankCert, int sessionId) {
		super(KeyExchangeMessage.MessageType.CertificateResponse);
		this.bankCert = bankCert;
		this.sequenceId = sessionId * G2Constants.SEQ_NUMBER_MULTIPLIER;
	}

	public SignedObject getBankCert() {
		return bankCert;
	}

	public int getSequenceId() {
		return sequenceId;
	}
	
	public int getSessionId() {
		return sequenceId / G2Constants.SEQ_NUMBER_MULTIPLIER;
	}
}
