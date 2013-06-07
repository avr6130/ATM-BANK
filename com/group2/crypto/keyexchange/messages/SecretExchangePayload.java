package com.group2.crypto.keyexchange.messages;

import java.io.Serializable;
import java.security.Key;

import com.group2.authority.G2Constants;

public class SecretExchangePayload implements Serializable {

	private String pin;
	private int sequenceId;
	private int accountNumber;
	private Key sessionKey;
	
	public SecretExchangePayload(int accountNumber, int sessionId, Key sessionKey, String pin) {
		this.accountNumber = accountNumber;
		this.sequenceId = sessionId * G2Constants.SEQ_NUMBER_MULTIPLIER;
		this.sessionKey = sessionKey;
		this.pin = pin;
	}

	public int getSessionId() {
		return sequenceId / G2Constants.SEQ_NUMBER_MULTIPLIER;
	}
	
	public int getSequenceId() {
		return sequenceId;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public Key getSessionKey() {
		return sessionKey;
	}
	
	public String getPin() {
		return pin;
	}

	@Override
	public String toString() {
		return "SecretExchangePayload [pin=" + pin + ", sequenceId=" + sequenceId
				+ ", accountNumber=" + accountNumber + ", sessionKey="
				+ sessionKey + "]";
	}
	
	

}
