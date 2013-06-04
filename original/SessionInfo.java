package original;

import java.security.Key;
import java.util.Timer;
import java.util.TimerTask;

public class SessionInfo {
	
	private static final long TIMEOUT_DELAY = 60000L;
	
	private int accountNumber = 0;
	
	private Key key = null;
	
	private boolean valid = true;
	
	private Timer timeoutTimer;
	
	public SessionInfo(int accountNumber, Key key) {
		this.accountNumber = accountNumber;
		this.key = key;
		Timer timeoutTimer = new Timer();
		
		// set a session timeout to invalidate the session
		timeoutTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				SessionInfo.this.valid = false;
			}
		}, TIMEOUT_DELAY);
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