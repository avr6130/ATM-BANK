package original;

import java.security.PrivateKey;

public class SessionInfo {
	
	private int accountNumber = 0;
	
	private PrivateKey key = null;
	
	public SessionInfo(int accountNumber, PrivateKey key) {
		this.accountNumber = accountNumber;
		this.key = key;
	}
	
	public int getAccountNumber() {
		return this.accountNumber;
	}
	
	public PrivateKey getKey() {
		return this.key;
	}
}