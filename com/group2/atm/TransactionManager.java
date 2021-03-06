package com.group2.atm;

import java.io.IOException;
import java.security.Key;

import com.group2.authority.G2Constants;
import com.group2.crypto.Keygen;
import com.group2.crypto.keyexchange.messages.AuthenticationMessage;
import com.group2.messaging.BalanceResponse;
import com.group2.messaging.WithdrawResponse;
import com.group2.util.Disk;
import com.group2.util.PropertiesFile;



/**
 * Created with IntelliJ IDEA.
 * User: Group2
 * Date: 5/25/13
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */

public class TransactionManager {
	private boolean transactionActive = false;
	private AtmCard atmCard;
	private int accountNumber = 0;
	private String pin;
	private Key sessionKey;
	private int sequenceId;
	
	public TransactionManager() {
		String algorithmName = PropertiesFile.getProperty(PropertiesFile.SESSION_ALGORITHM_NAME, "AES");
		int keysize = Integer.parseInt(PropertiesFile.getProperty(PropertiesFile.SESSION_ALGORITHM_KEYSIZE, "128"));
		this.sessionKey = (Key) Keygen.generateKey(algorithmName, keysize);
	}
	
	public String getPin() {
		return this.pin;
	}

	public int getSequenceId() {
		return this.sequenceId;
	}
	
	public int getSessionId() {
		return sequenceId / G2Constants.SEQ_NUMBER_MULTIPLIER;
	}

	public Key getSessionKey() {
		return this.sessionKey;
	}
	
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}
	
	public boolean isSeqenceIdValid(int msgSequenceId) {
		if (msgSequenceId > this.sequenceId) {
			this.sequenceId = msgSequenceId;
			return true;
		}
		return false;
	}

	public void endCurrentTransaction() {
		if (!transactionActive) {
			System.out.println("No user logged in");
			return;
		}
		transactionActive = false;
		System.out.println(atmCard.getName() + " logged out.");

	} // end setTransactionActive

	public int getActiveAccountNum() {
		return accountNumber;
	} // end getActiveAccountNum()

	public boolean transactionActive() {
		return transactionActive;
	} // end transactionActive()

	public boolean authenticateSession(String[] splitCmdString) throws IOException {
		// Check if more than one argument was read from stdin
		if (!(splitCmdString.length > 1)) {

			// Not enough command line arguments were given so set the
			// transaction state to inactive and the session request to null
			transactionActive = false;
			accountNumber = 0;
		} // end if length < 1
		// splitCmdString.length IS greater than 1 so multiple args were given
		else { 
			// Read the original.ATM card into a class variable
			atmCard = (AtmCard) Disk.load(splitCmdString[1] + ".card");

			// Card did not exist
			if (atmCard == null) {
				transactionActive = false;
				accountNumber = 0;
			}
			// Found card file
			else {
				// Check that the name within the card matches the given name
				if (!atmCard.getName().matches(splitCmdString[1])) {
					// User's name does not match the card or the file doesn't exist so set the
					// transaction state to inactive and the session request to null
					transactionActive = false;
					accountNumber = 0;
				} // end if (!atmCard.getName().matches(splitCmdString[1]))
				// All checks have passed so prompt for the pin
				else {
					// Get the required information out of the card and prepare the message
					this.accountNumber = atmCard.getAccountNumber();
					this.transactionActive = true;
				}
			}
		} // end else this IS a valid card file
		return this.transactionActive;
	} // end authenticateSession

	public void authenticationMessage(AuthenticationMessage authenticationMessage) {
		// Set the transaction state true or false
		if (transactionActive = authenticationMessage.isSessionValid() == true) {
			System.out.println("User " + atmCard.getName() + " is Authorized");
		} // end if transactionActive
		else {
			System.out.println("Unauthorized");
		} // end else

	} // end sessionResponse

	public void balanceResponse(BalanceResponse balanceResponse) {
		System.out.println("balance: $" + balanceResponse.getBalance());
	} // end processBalanceResponse

	public void withdrawResponse(WithdrawResponse payload) {
		double amt = payload.getWithdrawAmount();
		if (amt != 0) {
			System.out.println("$" + amt + " dispensed");
		} else {
			System.out.println("Insufficient funds.");
		}
	}

	@Override
	public String toString() {
		return "TransactionManager [transactionActive=" + transactionActive
				+ ", atmCard=" + atmCard + ", accountNumber=" + accountNumber
				+ ", sessionKey=" + sessionKey + ", squenceId=" + sequenceId
				+ "]";
	}

} // end class original.TransactionManager
