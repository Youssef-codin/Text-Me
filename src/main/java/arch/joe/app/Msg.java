package arch.joe.app;

import java.sql.Timestamp;

public class Msg {

    private final String msg;
    private final String msgSender;
    private final String msgReceiver;
    private final long timeStamp;

    public Msg(String msg, String msgSender, String msgReceiver) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        this.msg = msg;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
        this.timeStamp = ts.getTime();
    }

    public Msg(String msg, String msgSender, String msgReceiver, long time) {

        this.msg = msg;
        this.msgSender = msgSender;
        this.msgReceiver = msgReceiver;
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
}
