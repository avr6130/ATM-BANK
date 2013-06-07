package com.group2.messaging;

public class TerminationRequest extends Payload {

	public TerminationRequest(int accountNumber, int sequenceId) {
        super(accountNumber, sequenceId);
	}

}
