package com.group2.bank;

import java.security.Key;
import java.util.Timer;
import java.util.TimerTask;

import com.group2.authority.G2Constants;
import com.group2.util.PropertiesFile;



public class SessionInfo {
	
	private static final String DEFAULT_SESSION_TIMEOUT = "60";
	
	private int accountNumber = 0;
	
	private int sequenceId = 0;
	
	private Key key = null;
	
	private boolean valid = true;
	
	private Timer timeoutTimer;
	
	public SessionInfo(int accountNumber, int sessionId, Key key) {
		this.accountNumber = accountNumber;
		this.sequenceId = sessionId * G2Constants.SEQ_NUMBER_MULTIPLIER;
		this.key = key;
		timeoutTimer = new Timer();
		
		long delay = Long.parseLong(PropertiesFile.getProperty(PropertiesFile.SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT)) 
				* G2Constants.SEC_TO_MSEC;
		// set a session timeout to invalidate the session
		timeoutTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				SessionInfo.this.valid = false;
			}
		}, delay);
	}
	
	public int getAccountNumber() {
		return this.accountNumber;
	}
	
	public int getSequenceId() {
		return this.sequenceId;
	}
	
	public int getSessionId() {
		return this.sequenceId / G2Constants.SEQ_NUMBER_MULTIPLIER;
	}
	
	public Key getKey() {
		return this.key;
	}
	
	public boolean isValid() {
		synchronized (this.timeoutTimer) {
			return this.valid;
		}
	}
	
	public boolean isSeqenceIdValid(int msgSequenceId) {
		if (msgSequenceId > this.sequenceId) {
			this.sequenceId = msgSequenceId;
			return true;
		}
		return false;
	}
	
	public void terminateSession() {
		synchronized (this.timeoutTimer) {
			this.timeoutTimer.cancel();
			this.valid = false;
		}
	}
}