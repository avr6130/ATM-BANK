package bank;

import java.security.Key;
import java.util.Timer;
import java.util.TimerTask;

import util.PropertiesFile;

import authority.G2Constants;

public class SessionInfo {
	
	private static final String DEFAULT_SESSION_TIMEOUT = "60";
	
	private int accountNumber = 0;
	
	private Key key = null;
	
	private boolean valid = true;
	
	private Timer timeoutTimer;
	
	public SessionInfo(int accountNumber, Key key) {
		this.accountNumber = accountNumber;
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
	
	public Key getKey() {
		return this.key;
	}
	
	public boolean isValid() {
		synchronized (this.timeoutTimer) {
			return this.valid;
		}
	}
	
	public void terminateSession() {
		synchronized (this.timeoutTimer) {
			this.timeoutTimer.cancel();
			this.valid = false;
		}
	}
}