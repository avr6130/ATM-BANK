package messaging;

import original.SessionInfo;

public class SessionResponse extends Payload {

	private SessionInfo sessionInfo;
	
	public SessionResponse(int accountNumber, SessionInfo sessionInfo) {
		super(accountNumber);
		this.sessionInfo = sessionInfo;
	}
	
	public SessionInfo getSessionInfo() {
		return this.sessionInfo;
	}

}
