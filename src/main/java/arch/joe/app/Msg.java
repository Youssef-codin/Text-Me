package arch.joe.app;

import java.sql.Timestamp;

public class Msg {

    private final String msg;
    private final String msgSender;
    private final String aesSender;
    private final String msgReceiver;
    private final String aesReceiver;
    private final String aesIv;
    private final long timeStamp;

    public Msg(String msg, String msgSender, String msgReceiver, String aesSender, String aesReceiver, String aesIv) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        this.msg = msg;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
        this.aesSender = aesSender;
        this.aesReceiver = aesReceiver;
        this.aesIv = aesIv;
        this.timeStamp = ts.getTime();
    }

    public Msg(String msg, String msgSender, String msgReceiver, long time, String aesSender, String aesReceiver,
            String aesIv) {

        this.msg = msg;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
        this.aesSender = aesSender;
        this.aesReceiver = aesReceiver;
        this.aesIv = aesIv;
        this.timeStamp = time;
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
}
