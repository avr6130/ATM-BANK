package messaging;

import original.AccountManager;
import original.TransactionManager;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 9:43 PM
 */
public class MessageHandler {

    public void processMessage(Message msg) {

        Payload payload = msg.getPayload();

        if (payload == null) {
            System.err.println("Message does not contain payload");
            return;
        }

        if (payload instanceof SessionRequest) {
            processSessionRequest((SessionRequest) payload);
        } else if (payload instanceof BalanceRequest) {
            processBalanceRequest((BalanceRequest) payload);
        } else if (payload instanceof WithdrawRequest) {
            processWithdrawRequest((WithdrawRequest) payload);
        } else if (payload instanceof SessionResponse) {
            processSessionResponse((SessionResponse) payload);
        } else if (payload instanceof BalanceResponse) {
            processBalanceResponse((BalanceResponse) payload);
        } else if (payload instanceof WithdrawResponse) {
            processWithdrawResponse((WithdrawResponse) payload);
        } else {
            System.err.println("Unknown payload type");
        }

    }

    public void processSessionRequest(SessionRequest msg) {
        //TODO
    } // end processSessionRequest

    private void processWithdrawResponse(WithdrawResponse payload) {
        //TODO
    }

    private void processBalanceResponse(BalanceResponse payload) {
        //TODO
    }

    private void processSessionResponse(SessionResponse payload) {
    } // end processSessionResponse

    private void processWithdrawRequest(WithdrawRequest payload) {
        //TODO
    }

    private void processBalanceRequest(BalanceRequest payload) {
        //TODO
    }

//    private void processSessionRequest(SessionRequest payload) {
//        //TODO
//    }
}
