package com.group2.messaging;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:24 PM
 */
public class BalanceRequest extends Payload {

    public BalanceRequest(int accountNumber, int sequenceId) {
        super(accountNumber, sequenceId);
    }
}
