package original;

import messaging.*;

import java.io.*;
import java.net.Socket;

/**
 * A BankProtocol processes local and remote commands sent to the Bank and writes to
 * or reads from the router as necessary. You can use whatever method you would like to
 * read from and write to the router, but this is an example to get you started.
 */

public class BankProtocol implements Protocol {

    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    // ########### temporary #### delete me ######################
    private int tempAcctNumber = 0;

    public BankProtocol(Socket socket) throws IOException {

        writer = new ObjectOutputStream(socket.getOutputStream());
        reader = new ObjectInputStream(socket.getInputStream());
    }

    /* Process commands sent through the router. */
    public void processRemoteCommands(String prompt) throws IOException {
        Message msgObject;

        try {
            while ((msgObject = (Message) reader.readObject()) != null) {
                processRemoteCommand(msgObject);

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

    /* Process commands from the command line. */
    public void processLocalCommands(BufferedReader stdIn, String prompt) throws IOException {
        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            processLocalCommand(userInput);
        }

        stdIn.close();
    }

    /* Process a remote command. */
    //private synchronized void processRemoteCommand(String command) {
    private synchronized void processRemoteCommand(Message messageObject) {
        boolean authenticated = false;
        Message msg = new Message();

        //authenticated = messageHandler.processMessage(messageObject);
        

        // ########### temporary #####################################
        // This is temporary but useful for initial testing of message exchange.
        // The functionality can probably be used almost as is, but in the proper place.
        // Now send the session response back to the ATM
        try {

            Payload payload = messageObject.getPayload();
            if (payload instanceof AuthenticationRequest) {
                AccountManager accountManager = new AccountManager();
                authenticated = accountManager.authenticateRequest((AuthenticationRequest) payload);
                tempAcctNumber = payload.getAccountNumber();

                // Create the SessionResponse object and give it the account number and result of PIN validation.
                SessionResponse sessionResponse = new SessionResponse(messageObject.getPayload().getAccountNumber(), authenticated);

                // Set the message payload to the sessionResponse object
                msg.setPayload(sessionResponse);

                // Send the message back to the ATM
                writer.writeObject(msg);

            } // end AuthenticationRequest

            // This is completely fake, but is here to test that basic balance response is
            // handled properly on the ATM side.
            else if (payload instanceof BalanceRequest) {
                BalanceResponse balanceResponse = new BalanceResponse(tempAcctNumber, AccountManager.processBal(tempAcctNumber));
                msg.setPayload(balanceResponse);
                writer.writeObject(msg);

            } // end if BalanceResponse
            else if (payload instanceof WithdrawRequest) {
            	double amt = ((WithdrawRequest) payload).getWithdrawAmount();
            	int acctNum = payload.getAccountNumber();
            	AccountManager.processWith(acctNum, amt);
                WithdrawResponse withdarwResponse = new WithdrawResponse(acctNum, amt);
                msg.setPayload(withdarwResponse);
        		this.writer.writeObject(msg);
            } // end if WithdrawRequest

        } catch (IOException e) {
            e.printStackTrace();
            e.getMessage();
        } // end catch
        // ########### end temporary section #####################################

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
