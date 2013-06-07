package com.group2.messaging;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:01 PM
 */
public abstract class Payload implements Serializable {

    private int accountNumber = 0;
    
    private int sequenceId = 0;

    public Payload(int accountNumber, int sequenceId) {
        this.accountNumber = accountNumber;
        this.sequenceId = sequenceId + 1;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
    
    public int getSequenceId() {
    	return sequenceId;
    }

	@Override
	public String toString() {
		return "Payload [accountNumber=" + accountNumber + ", sequenceId="
				+ sequenceId + "]";
	}
    
}
