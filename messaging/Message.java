package messaging;

import java.io.Serializable;

import javax.crypto.SealedObject;

/**
 * Created with IntelliJ IDEA.
 * User: Team2
 * Date: 5/24/13
 * Time: 8:59 PM
 */
public class Message implements Serializable {

    private int sessionId = 0;

    private SealedObject sealedPayload = null;

    public int getSessionID() {
        return sessionId;
    }

    /**
     * @param sessionId
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public SealedObject getSealedPayload() {
        return this.sealedPayload;
    }

    public void setSealedPayload(SealedObject sealedPayload) {
        this.sealedPayload = sealedPayload;
    }
}
