// Used SourceTree to create a Git repository and added to GitHub

import java.io.*;


public class Account implements Serializable {
    private String name;
    private int acctno;
    private int pinno;
    private double balance;
    private AtmCardClass atmCard;
    private long nextValidLoginTime = System.currentTimeMillis();
    private short currentNumOfFailedLoginAttempts = 0;

    Account(String Name, int acct, int pin, double bal) {
        name = Name;
        acctno = acct;
        pinno = pin;
        balance = bal;
        atmCard = new AtmCardClass(name, acctno);
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

    public void print() {
        System.out.println("Acct# " + acctno + "\n" + "Pin# " + pinno + "\n" + "Name " + name + "\n" + "Acct Bal " + balance + "\n");
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return acctno;
    }

    public int getPin() {
        return pinno;
    }

    public double getBal() {
        return balance;
    }

    public AtmCardClass getAtmCard() {
        return atmCard;
    }

    public void setName(String Name) {
        name = new String(Name);
    }

    public void setId(int ID) {
        acctno = ID;
    }

    public void setPin(int Pin) {
        pinno = Pin;
    }

    public void setBal(double Bal) {
        balance = Bal;
    }


}
