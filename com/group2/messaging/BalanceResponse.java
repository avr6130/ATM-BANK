package com.group2.messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:29 PM
 */
public class BalanceResponse extends Payload {

    private double balance = 0;

    public BalanceResponse(int accountNumber, int sequenceId, double balance) {
        super(accountNumber, sequenceId);
        this.balance = balance;
    }

    public double getBalance() {
        return this.balance;
    }
}
