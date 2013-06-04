package original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import messaging.AuthenticationRequest;
import messaging.AuthenticationResponse;
import messaging.BalanceRequest;
import messaging.BalanceResponse;
import messaging.Message;
import messaging.Payload;
import messaging.TerminationRequest;
import messaging.TerminationResponse;
import messaging.WithdrawRequest;
import messaging.WithdrawResponse;
import crypto.CryptoAES;
import crypto.keyexchange.KeyExchangeSupport;
import crypto.keyexchange.KeyExchangeSupport.AppMode;
import crypto.keyexchange.messages.CertificateResponseMessage;
import crypto.keyexchange.messages.KeyExchangeMessage;
import crypto.keyexchange.messages.SecretExchangeMessage;
import crypto.keyexchange.messages.SecretExchangePayload;

/**
 * A BankProtocol processes local and remote commands sent to the Bank and writes to
 * or reads from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class BankProtocol implements Protocol {

	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private HashMap<Integer, SessionInfo> sessionMap;
	private int sessionIdCounter = 1;
	private AccountManager accountManager;
	private KeyExchangeSupport keyExchangeSupport;

	public BankProtocol(Socket socket) throws IOException {

		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
		sessionMap = new HashMap<Integer, SessionInfo>();
		accountManager = new AccountManager();
		keyExchangeSupport = new KeyExchangeSupport(AppMode.BANK);
	}

	/* Process commands sent through the router. */
	public void processRemoteCommands(String prompt) throws IOException {
		Object msgObject;

		try {
			while ((msgObject = reader.readObject()) != null) {
				
				if (msgObject instanceof Message) {
					this.processMessage((Message) msgObject);
				}
				else if(msgObject instanceof KeyExchangeMessage) {
					this.processMessage((KeyExchangeMessage) msgObject);
				}

				// This was inserted here because the command line reader thread is
				// currently blocked waiting for input.  That means if the Bank ever outputs
				// an informational message, the prompt "Bank: " will not be shown unless it
				// is output here.  Alternatively, it could be removed from here and placed
				// as the last line of output anywhere the Bank prints a line.
				System.out.print(prompt);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void processMessage(KeyExchangeMessage msgObject) {
		KeyExchangeMessage responseMsg = null;
		if (msgObject.mType == KeyExchangeMessage.MessageType.InitiateExchange) {
			int sessionId = this.sessionIdCounter++;
			this.sessionMap.put(Integer.valueOf(sessionId), null);
			responseMsg = new CertificateResponseMessage(this.keyExchangeSupport.getBankCertificate(), sessionId);
		}
		else if (msgObject.mType == KeyExchangeMessage.MessageType.SecretExchange) {
			
			SecretExchangePayload payload = (SecretExchangePayload) keyExchangeSupport.decryptSecret(((SecretExchangeMessage) msgObject).getSecret());
			Integer sessionId = Integer.valueOf(payload.getSessionId());
			if (this.sessionMap.containsKey(sessionId)) {
				SessionInfo sessionInfo = new SessionInfo(payload.getAccountNumber(), payload.getSessionKey());
				this.sessionMap.put(sessionId, sessionInfo);
			} else {
				//TODO log error?
			}
			
		}
		else {
			//TODO bad message type
		}
		if (responseMsg != null) {
			try {
				this.writer.writeObject(responseMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Process commands from the command line. */
	public void processLocalCommands(BufferedReader stdIn, String prompt) throws IOException {
		String userInput;

		while ((userInput = stdIn.readLine()) != null) {
			processLocalCommand(userInput);
			System.out.print(prompt);
		}

		stdIn.close();
	}

	private Payload generateAuthenticationResponse(Integer sessionId, AuthenticationRequest authRequest) {
		boolean authenticated = this.accountManager.authenticateRequest(authRequest);

		// Create the SessionResponse object and give it the account number and result of PIN validation.
		return new AuthenticationResponse(authRequest.getAccountNumber(), authenticated);
	}
	
	private Payload generateBalanceResponse(Integer sessionId, BalanceRequest balanceRequest) {
		return new BalanceResponse(balanceRequest.getAccountNumber(), AccountManager.processBal(balanceRequest.getAccountNumber()));
	}
	
	private Payload generateWithdrawResponse(Integer sessionId, WithdrawRequest withdrawRequest) {
		double amt = withdrawRequest.getWithdrawAmount();
		int acctNum = withdrawRequest.getAccountNumber();
		
		if (AccountManager.processWith(acctNum, amt)) {
			return new WithdrawResponse(acctNum, amt);
		} else {
			return new WithdrawResponse(acctNum, 0.0);
		}
	}

	private synchronized void processMessage(Message messageObject) {
		Payload responsePayload = null;
		Message responseMessage = new Message();
		
		Integer sessionId = Integer.valueOf(messageObject.getSessionID());
		
		SessionInfo sessionInfo = this.sessionMap.get(sessionId);
		
		// the bank does not have record of the message's sessionId
		if (sessionInfo == null) {
			//TODO log error/respond to message
			return;
		}
		
		// try to decrypt the sealed payload
		Payload requestPayload = CryptoAES.decrypt(sessionInfo.getKey(), messageObject.getSealedPayload());
		
		if (requestPayload == null || requestPayload.getAccountNumber() != sessionInfo.getAccountNumber()) {
			//TODO log error/respond to message
			return;
		}
		// process message
		else if (!sessionInfo.isValid() || requestPayload instanceof TerminationRequest) {
			responsePayload = this.generateTerminationResponse(sessionId);
		}
		else if (requestPayload instanceof AuthenticationRequest) {
			responsePayload = this.generateAuthenticationResponse(sessionId, (AuthenticationRequest) requestPayload);
		}
		else if (requestPayload instanceof BalanceRequest) {
			responsePayload = this.generateBalanceResponse(sessionId, (BalanceRequest) requestPayload);
		}
		else if (requestPayload instanceof WithdrawRequest) {
			responsePayload = this.generateWithdrawResponse(sessionId, (WithdrawRequest) requestPayload);
		}
		responseMessage.setSessionId(sessionId.intValue());
		responseMessage.setSealedPayload(CryptoAES.encrypt(sessionInfo.getKey(), responsePayload));
		try {
			this.writer.writeObject(responseMessage);
		} catch (IOException e) {
			e.printStackTrace();
			e.getMessage();
		}

	}

	private Payload generateTerminationResponse(Integer sessionId) {
		SessionInfo sessionInfo = this.sessionMap.remove(sessionId);
		sessionInfo.terminateSession();
		return new TerminationResponse(sessionInfo.getAccountNumber());
	}

	/* Process user input. */
	private synchronized void processLocalCommand(String command) {

		String[] parsedCommand = command.split("\\s");

		// format <command> <name>
		if(parsedCommand.length != 2) 
			return;

		// parse command and customer name
		command = parsedCommand[0];
		String name = parsedCommand[1];

		int acctNum = AccountManager.lookAcctByName(name);

		if (AccountManager.isAcctNumValid(acctNum)) {
			if (command.toLowerCase().matches("balance")) {
				System.out.println("balance: $"+AccountManager.processBal(acctNum));
			} else if (command.toLowerCase().matches("deposit")) {
				double amt = promptForDeposit(); 
				if (amt > 0){
					AccountManager.processDep(acctNum, amt);
					System.out.println("$" + amt + " added to " + name + "'s account");
				}
			} else if (command.toLowerCase().matches("withdraw")) {
				System.out.println("Withdrawals from the bank are not supported.");

			} else if (command.toLowerCase().matches("validate")) {
				System.out.println("validate entered");

			} else {
				System.out.println("Illegal input entered");
			} // end else
		}
	}

	private double promptForDeposit() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		double amt = 0.0;
		System.out.println("Enter the amount to deposit:");

		//  read the amount to deposit from the command-line
		try {
			amt = Double.parseDouble(br.readLine());
		} catch (IOException ioe) {

		} catch (NumberFormatException e) {

		}

		return amt;
	}

	/* Clean up all open streams. */
	public void close() throws IOException {
		reader.close();
		writer.close();
	}
}
