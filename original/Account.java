package original;

import java.io.*;


public class Account implements Serializable {
    private String name;
    private int acountNumber;
    private int pin;
    private double balance;
    private AtmCardClass atmCard;
    private long nextValidLoginTime = System.currentTimeMillis();
    private short currentNumOfFailedLoginAttempts = 0;

    Account(String name, int accountNumber, int pin, double balance) {
        this.name = name;
        this.acountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        atmCard = new AtmCardClass(this.name, this.acountNumber);
    }

    public void incrementCurrentNumOfFailedLoginAttempts() {
        currentNumOfFailedLoginAttempts++;
    }

    public void resetCurrentNumOfFailedLoginAttempts() {
        currentNumOfFailedLoginAttempts = 0;
    }

    public short getCurrentNumOfFailedLoginAttempts() {
        return currentNumOfFailedLoginAttempts;
    }

    public void setNextValidLoginTime(long nextValidLoginTime) {
        this.nextValidLoginTime = nextValidLoginTime;
    }

    public long getNextValidLoginTime() {
        return nextValidLoginTime;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return acountNumber;
    }

    public int getPin() {
        return pin;
    }

    public double getBal() {
        return balance;
    }

    public AtmCardClass getAtmCard() {
        return atmCard;
    }
    
    public void setBalance(double balance) {
    	this.balance = balance;
    }

	@Override
	public String toString() {
		return "Account [name=" + name + ", acountNumber=" + acountNumber
				+ ", pin=" + pin + ", balance=" + balance + ", atmCard="
				+ atmCard + ", nextValidLoginTime=" + nextValidLoginTime
				+ ", currentNumOfFailedLoginAttempts="
				+ currentNumOfFailedLoginAttempts + "]";
	}
    
    
}
