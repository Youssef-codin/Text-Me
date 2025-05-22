package arch.joe.app;

public class Msg {

    private int msgId;
    private String msg;
    private final String msgSender;
    private final String aesSender;
    private final String msgReceiver;
    private final String aesReceiver;
    private final String aesIv;
    private final long timeStamp;
    private boolean sent;

    public Msg(String msg, String msgSender, String msgReceiver, String aesSender, String aesReceiver, String aesIv) {

        this.msg = msg;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
        this.aesSender = aesSender;
        this.aesReceiver = aesReceiver;
        this.aesIv = aesIv;
        this.timeStamp = System.currentTimeMillis();
        this.sent = false;
    }

    public Msg(int msgId, String msg, String msgSender, String msgReceiver, long time, String aesSender,
            String aesReceiver, String aesIv, boolean sent) {

        this.msg = msg;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
        this.aesSender = aesSender;
        this.aesReceiver = aesReceiver;
        this.aesIv = aesIv;
        this.timeStamp = time;
        this.sent = sent;
        this.msgId = msgId;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getMsg() {
        return msg;
    }

    public String getMsgSender() {
        return msgSender;
    }

    public String getMsgReceiver() {
        return msgReceiver;
    }

    public long msgTime() {
        return timeStamp;
    }

    public String getAesSender() {
        return aesSender;
    }

    public String getAesReceiver() {
        return aesReceiver;
    }

    public String getAesIv() {
        return aesIv;
    }

    public boolean getSent() {
        return sent;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

}
