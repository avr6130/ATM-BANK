package original;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import authority.G2Constants;
import crypto.keyexchange.messages.SecretExchangePayload;

/**
 * Created with IntelliJ IDEA.
 * User: Group2
 * Date: 5/21/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */


public class AccountManager {

	private final short MAX_FAILED_ATTEMPTS = Short.parseShort(
			PropertiesFile.getProperty(PropertiesFile.LOGIN_ATTEMPTS, "3"));
	private final long LOCKOUT_DURATION = Integer.parseInt(
			PropertiesFile.getProperty(PropertiesFile.LOCKOUT_DURATION, "60")) * G2Constants.SEC_TO_MSEC;

	private static HashMap<Integer, Account> acctMap = new HashMap<Integer, Account>();

	public void createAccounts() throws IOException {
		this.createAccount("Alice", 16849327, "1095", 100.0);
		this.createAccount("Bob", 12049343, "3673", 100.0);
		this.createAccount("Carol", 30414389, "6251", 0.0);

	}

	public void createAccount(String customerName, int accountNumber, String pin, double initialBalance) throws IOException {

		try {
			// Create the account, including an ATM card internal to constructor.
			Account newAcct = new Account(customerName, accountNumber, pin, initialBalance);

			// Add Accounts to bank
			acctMap.put(newAcct.getID(), newAcct);

			createAtmCardFile(newAcct.getAtmCard());
		} catch (IOException e) {
			if (PropertiesFile.isDebugMode()) {
				e.printStackTrace();
			}
		}
	} // end createAccount

	public void retrieveAllAccounts() throws IOException {
		Object obj = Disk.load("accountsFile");
		if (obj instanceof HashMap) {
			acctMap = (HashMap<Integer, Account>) obj;
		}
		if (PropertiesFile.isDebugMode()) {
			System.out.println(acctMap);
		}
	} // end retrieveAllAccounts

	public void storeAllAccounts() throws IOException {
		Disk.save((Serializable) acctMap, "accountsFile");
		if (PropertiesFile.isDebugMode()) {
			System.out.println(acctMap);
		}
	} // end storeAllAccounts

	public void createAtmCardFile(AtmCard atmCard) throws IOException {
		Disk.save(atmCard, atmCard.getName() + ".card");
	} // end createAtmCardFile

	public AtmCard retrieveAtmCard(String name) throws IOException {
		return (AtmCard) Disk.load(name + ".card");
	} // end retrieveCard

	public boolean authenticateSession(SecretExchangePayload sessionPayload) {

		boolean authorized = false;

		Account currAcct = acctMap.get(sessionPayload.getAccountNumber());

		if (currAcct.getNextValidLoginTime() > System.currentTimeMillis()) {

			System.out.println("\nRemote command processed.  This account is currently locked out.  Try again later.");

			// Fail authentication/validation and return immediately
			authorized = false;

		} // end if getNextValidationTime
		else if (currAcct.getCurrentNumOfFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {

			System.out.println("\nRemote command processed.  MAX_FAILED_LOGIN_ATTEMPTS.");

			// Set the valid login time into the future to avoid repetitive false login attempts
			currAcct.setNextValidLoginTime(LOCKOUT_DURATION + System.currentTimeMillis());

			// Now that a lockout time has been set, reset the number of failed login attempts
			currAcct.resetCurrentNumOfFailedLoginAttempts();

			authorized = false;
		} // end if now MAX_FAILED_ATTEMPTS

		// Check the pin
		else if (!currAcct.getPin().equals(sessionPayload.getPin())) {
			System.out.println("\nRemote command processed. PIN didn't match.");
			currAcct.incrementCurrentNumOfFailedLoginAttempts();

			authorized = false;

		} // end if entered pin != pin
		else { // The pin entered must have been good
			System.out.println("\nRemote command processed.  AUTHENTICATED.");
			currAcct.resetCurrentNumOfFailedLoginAttempts();
			authorized = true;
		} // end else -> entered pin is correct

		try {
			this.storeAllAccounts();
		} catch (IOException e) {
			if (PropertiesFile.isDebugMode()) {
				e.printStackTrace();
			}
		}
		return authorized;
	} // end authenticateRequest()

	public static int lookAcctByName(String name) {
		int acctNumber = 0;

		for (Integer acctNo: acctMap.keySet()) {
			Account user = acctMap.get(acctNo);
			if (user.getName().equalsIgnoreCase(name)){
				acctNumber = user.getID();
			}
		}
		return acctNumber;
	}

	public static void processDep(int acctNo, double amt)
	{
		Account acct = acctMap.get(acctNo);
		acct.setBalance(acct.getBal() + amt);
		acctMap.put(acctNo, acct);
	}


	public static boolean processWith(int acctNo,double amt) {
		Account acct = acctMap.get(acctNo);

		//check if $$$ in the bank
		if (acct.getBal() >= amt) {
			acct.setBalance(acct.getBal() - amt);
			acctMap.put(acctNo, acct);
			return true;
		} else {
			return false;
		}
	}


	public static double processBal(int acctNo) {
		if (acctMap.containsKey(acctNo)){
			return acctMap.get(acctNo).getBal();
		}
		return 0;
	}

	public static boolean isAcctNumValid(int acctNum) {
		return acctMap.containsKey(acctNum);
	}

} // class AccountManager
