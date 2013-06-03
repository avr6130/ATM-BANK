package original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;

import messaging.AuthenticationRequest;
import messaging.AuthenticationResponse;
import messaging.BalanceRequest;
import messaging.BalanceResponse;
import messaging.Message;
import messaging.Payload;
import messaging.WithdrawRequest;
import messaging.WithdrawResponse;
import authority.G2Constants;
import crypto.CryptoAES;
import crypto.keyexchange.KeyExchangeSupport;
import crypto.keyexchange.KeyExchangeSupport.AppMode;
import crypto.keyexchange.messages.CertificateResponseMessage;
import crypto.keyexchange.messages.KeyExchangeMessage;

/**
 * An ATMProtocol processes local splitStr[0]s sent to the ATM and writes to or reads
 * from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class ATMProtocol implements Protocol {

    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private TransactionManager atmTransactionManager;
    private AuthenticationRequest authenticationRequest;
    private KeyExchangeSupport keyExchangeSupport;

    public ATMProtocol(Socket socket) throws IOException {
        writer = new ObjectOutputStream(socket.getOutputStream());
        reader = new ObjectInputStream(socket.getInputStream());
		keyExchangeSupport = new KeyExchangeSupport(AppMode.ATM);
}

    /* Continue to read input until terminated. */
    public void processLocalCommands(BufferedReader stdIn, String prompt) throws IOException {
        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            processCommand(userInput);
            System.out.print(prompt);
        }

        stdIn.close();
    }

    private void processCommand(String command) throws IOException {

        // Split the input command on whitespace
        String[] splitCmdString = command.split("\\s+");
        
        Message requestMessage = null;

        if (splitCmdString[0].toLowerCase().matches("begin-session")) {
        	requestMessage = this.generateAuthenticationRequest(splitCmdString);

        } // end if begin-session request
        else if (splitCmdString[0].toLowerCase().matches("balance")) {
        	requestMessage = this.generateBalanceRequest();
        } // end else if balance request
        else if (splitCmdString[0].toLowerCase().matches("withdraw")) {
        	requestMessage = this.generateWithdrawRequest();  	
        } else if (splitCmdString[0].toLowerCase().matches("end-session")) {

            atmTransactionManager.endCurrentTransaction();

        } // end else if end-session

        else {
            System.out.println("Illegal input entered");
        } // end else
        
        try {
        	if (requestMessage != null) {
	            writer.writeObject(requestMessage);
	
	            // After the message is set to bank, prepare to process the response and block
	            processRemoteCommands();
        	}

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();   //To change body of catch statement use File | Settings | File Templates.
        } // end catch

    } // end processCommand

    private Message generateAuthenticationRequest(String[] splitCmdString) throws IOException {
    	
    	Message msg = null;
    	if (atmTransactionManager != null && atmTransactionManager.transactionActive()) {
            System.out.println("Transaction currently in progress.  Please end-session before beginning a new session.");
            return msg;
        } // end if transactionActive()
    	
    	// create new transaction manager
    	atmTransactionManager = new TransactionManager();
    	authenticationRequest = atmTransactionManager.authenticateSession(splitCmdString);

    	if (authenticationRequest == null) {
    		System.out.println("Unauthorized");
    		return msg;
    	} else {
    		msg = new Message();
    		msg.setSealedPayload(CryptoAES.encrypt(atmTransactionManager.getSessionKey(), authenticationRequest));
    		return msg;
    	}
 
	}

	private Message generateWithdrawRequest() {
		Message msg = null;
		
    	if (!atmTransactionManager.transactionActive()) {
            System.out.println("No user logged in");
            return msg;
        }
    	
		double amt = promptForWithdraw();
		if (amt > 0) {
			msg = new Message();
	        WithdrawRequest withdrawRequest = new WithdrawRequest(atmTransactionManager.getActiveAccountNum(), amt);

    		msg.setSealedPayload(CryptoAES.encrypt(atmTransactionManager.getSessionKey(), withdrawRequest));
	         return msg;
		}
		return msg;
	}

	private Message generateBalanceRequest() {
		Message msg = null;
		if (!atmTransactionManager.transactionActive()) {
			System.out.println("No user logged in");
			return msg;
		}

		BalanceRequest balanceRequest = new BalanceRequest(atmTransactionManager.getActiveAccountNum());
		
		msg = new Message();
		msg.setSealedPayload(CryptoAES.encrypt(atmTransactionManager.getSessionKey(), balanceRequest));
		return msg;
	}

	public void processRemoteCommands() throws IOException {
        Object msgObject;

        try {
            msgObject = (Object) reader.readObject();
            
            if (msgObject instanceof Message) {
				this.processMessage((Message) msgObject);
			}
			else if(msgObject instanceof KeyExchangeMessage) {
				this.processMessage((KeyExchangeMessage) msgObject);
			}

            

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } // end catch

    }
    
	private void processMessage(KeyExchangeMessage msgObject) {

		if (msgObject.mType == KeyExchangeMessage.MessageType.CertificateResponse) {
			
			Key bankPublicKey = this.keyExchangeSupport.validateCertificate(((CertificateResponseMessage) msgObject).getBankCert(), G2Constants.BANK_NAME);
			if (bankPublicKey != null) {
				
				this.keyExchangeSupport.encryptSecret(secret, bankPublicKey);
			}
		}
		else {
			//TODO bad message type
			return;
		}
	}

	private void processMessage(Message msgObject) {
		// Pull the payload out of the generic message.  The payload is the
        // specific message type.
        Payload payload = CryptoAES.decrypt(this.atmTransactionManager.getSessionKey(), msgObject.getSealedPayload());

        if (payload instanceof AuthenticationResponse) {
            atmTransactionManager.authenticationResponse((AuthenticationResponse) payload);
        } // end AuthenticationResponse

        else if (payload instanceof BalanceResponse) {
            atmTransactionManager.balanceResponse((BalanceResponse) payload);
        } // end BalanceResponse
        else if (payload instanceof WithdrawResponse) {
        	atmTransactionManager.withdrawResponse((WithdrawResponse) payload);
        }		
	}

	private double promptForWithdraw() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		double amt = 0.0;
		System.out.println("Enter the amount to withdraw:");

		// read the amount to withdraw from the command-line
		try {
			amt = Double.parseDouble(br.readLine());
		} catch (IOException ioe) {

		} catch (NumberFormatException e) {

		}

		return amt;
	}

    /* Clean up all open streams. */
    public void close() throws IOException {
        writer.close();
        reader.close();
    }

}