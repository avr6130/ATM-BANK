package crypto.keyexchange.messages;

import java.io.Serializable;

import crypto.Certificate;

public class CertificateResponseMessage extends KeyExchangeMessage implements Serializable {

	private Certificate bankCert;
	private int sessionId;

	public CertificateResponseMessage(Certificate bankCert, int sessionId) {
		super(KeyExchangeMessage.MessageType.CertificateResponse);
		this.bankCert = bankCert;
		this.sessionId = sessionId;
	}

	public Certificate getBankCert() {
		return bankCert;
	}

	public int getSessionId() {
		return sessionId;
	}
}
