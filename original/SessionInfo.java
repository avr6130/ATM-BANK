package original;

import java.security.Key;

public class SessionInfo {
	
	private int accountNumber = 0;
	
	private Key key = null;
	
	public SessionInfo(int accountNumber, Key key) {
		this.accountNumber = accountNumber;
		this.key = key;
	}
	
	public int getAccountNumber() {
		return this.accountNumber;
	}
	
	public Key getKey() {
		return this.key;
	}
}