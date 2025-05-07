package arch.joe.app;

public class Contact {

    private String name;
    private boolean isOnline;
    private String lastMsg;
    private String timeStamp;

    public Contact(String name, Boolean isOnline, String lastMsg, String timeStamp) {
        this.name = name;
        this.isOnline = isOnline;
        this.lastMsg = lastMsg;
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
