package original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.HashMap;

import messaging.AuthenticationRequest;
import messaging.AuthenticationResponse;
import messaging.BalanceRequest;
import messaging.BalanceResponse;
import messaging.Message;
import messaging.Payload;
import messaging.SessionRequest;
import messaging.WithdrawRequest;
import messaging.WithdrawResponse;

/**
 * A BankProtocol processes local and remote commands sent to the Bank and writes to
 * or reads from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class BankProtocol implements Protocol {

	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private HashMap<Integer, SessionInfo> sessionMap;
	private int sessionIDcounter = 1;
	private AccountManager accountManager;

	public BankProtocol(Socket socket) throws IOException {

		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
		sessionMap = new HashMap<Integer, SessionInfo>();
		accountManager = new AccountManager();
	}

	/* Process commands sent through the router. */
	public void processRemoteCommands(String prompt) throws IOException {
		Message msgObject;

		try {
			while ((msgObject = (Message) reader.readObject()) != null) {
				processRemoteCommand(msgObject);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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

	private Message generateAuthenticationResponse(Integer sessionId, AuthenticationRequest authRequest) {
		Message msg = new Message();
		boolean authenticated = this.accountManager.authenticateRequest(authRequest);

		// Create the SessionResponse object and give it the account number and result of PIN validation.
		AuthenticationResponse authenticationResponse = new AuthenticationResponse(authRequest.getAccountNumber(), authenticated);

		msg.setSessionID(sessionId);
		// Set the message payload to the authenticationResponse object
		msg.setPayload(authenticationResponse);

		return msg;
	}

	private Message generateSessionResponse(Integer sessionId, SessionRequest sessionRequest) {
		Message msg = new Message();
		PrivateKey key = null;  //TODO instantiate private key
		SessionInfo sessionInfo = null;

		if (this.sessionMap.containsKey(sessionId)) {
			System.err.println("Session request received with existing session ID.");
			sessionInfo = this.sessionMap.get(sessionId); //TODO handle session request with existing session id
		} else {
			sessionId = Integer.valueOf(this.sessionIDcounter++);
			sessionInfo = new SessionInfo(sessionRequest.getAccountNumber(), key);
			this.sessionMap.put(sessionId, sessionInfo);
		}
		msg.setSessionID(sessionId);
		msg.setPayload(sessionRequest);

		return msg;
	}
	
	private Message generateBalanceResponse(Integer sessionId, BalanceRequest balanceRequest) {
		Message msg = new Message();
		
		BalanceResponse balanceResponse = new BalanceResponse(balanceRequest.getAccountNumber(), AccountManager.processBal(balanceRequest.getAccountNumber()));
		
		msg.setSessionID(sessionId);
		msg.setPayload(balanceResponse);
		
		return msg;
	}
	
	private Message generateWithdrawResponse(Integer sessionId, WithdrawRequest withdrawRequest) {
		Message msg = new Message();
		
		double amt = withdrawRequest.getWithdrawAmount();
		int acctNum = withdrawRequest.getAccountNumber();
		WithdrawResponse withdrawResponse;
		if (AccountManager.processWith(acctNum, amt)) {
			withdrawResponse = new WithdrawResponse(acctNum, amt);
		} else {
			withdrawResponse = new WithdrawResponse(acctNum, 0.0);
		}
		
		msg.setSessionID(sessionId);
		msg.setPayload(withdrawResponse);
		
		return msg;
	}

	private synchronized void processRemoteCommand(Message messageObject) {
		Integer sessionId = Integer.valueOf(messageObject.getSessionID());
		Payload payload = messageObject.getPayload();
		Message responseMessage = null;
		
		if (payload instanceof SessionRequest) {
			responseMessage = this.generateSessionResponse(sessionId, (SessionRequest) payload);
		}
		else if (payload instanceof AuthenticationRequest) {
			responseMessage = this.generateAuthenticationResponse(sessionId, (AuthenticationRequest) payload);
		}
		else if (payload instanceof BalanceRequest) {
			responseMessage = this.generateBalanceResponse(sessionId, (BalanceRequest) payload);
		}
		else if (payload instanceof WithdrawRequest) {
			responseMessage = this.generateWithdrawResponse(sessionId, (WithdrawRequest) payload);
		}

		try {
			//TODO encrypted payload
			this.writer.writeObject(responseMessage);
		} catch (IOException e) {
			e.printStackTrace();
			e.getMessage();
		} // end catch

	} // end processRemoteCommand

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
