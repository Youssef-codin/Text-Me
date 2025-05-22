package arch.joe.db;

import arch.joe.app.Msg;

import java.sql.*;
import java.util.ArrayList;

public class MessageDao {

    private MessageDao() {

    }

    public static void insertMsg(Msg msg) {

        String sender = msg.getMsgSender();
        String receiver = msg.getMsgReceiver();
        String message = msg.getMsg();
        String aesSender = msg.getAesSender();
        String aesReceiver = msg.getAesReceiver();
        String aesIv = msg.getAesIv();
        long time = msg.msgTime();
        boolean sent = msg.getSent();

        try (Connection conn = Database.connect()) {
            Statement sm = conn.createStatement();
            sm.execute("PRAGMA foreign_keys = ON");

            PreparedStatement ps = conn
                    .prepareStatement(
                            "INSERT INTO msgs(msg_sender, msg_receiver, msg, time_stamp, aes_sender, aes_receiver, aes_iv, sent) values(?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, message);
            ps.setLong(4, time);
            ps.setString(5, aesSender);
            ps.setString(6, aesReceiver);
            ps.setString(7, aesIv);
            ps.setBoolean(8, sent);

            int rows = ps.executeUpdate();
            System.out.println("Changed: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 19) {
                System.out.println("User does not exist.");
            }
        }
    }

    public static ArrayList<Msg> getMsgsSent(String sender, String receiver) {

        ArrayList<Msg> messages = new ArrayList<>();

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM msgs WHERE (msg_sender = ? AND msg_receiver = ? AND sent = TRUE) OR (msg_sender = ? AND msg_receiver = ? AND sent = TRUE) ORDER BY time_stamp ASC");
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, receiver);
            ps.setString(4, sender);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int msgId = rs.getInt("msg_id");
                String message = rs.getString("msg");
                String send = rs.getString("msg_sender");
                String receive = rs.getString("msg_receiver");
                String aesSender = rs.getString("aes_sender");
                String aesReceiver = rs.getString("aes_receiver");
                String aesIv = rs.getString("aes_iv");
                long time = rs.getLong("time_stamp");
                boolean sent = rs.getBoolean("sent");
                messages.add(new Msg(msgId, message, send, receive, time, aesSender, aesReceiver, aesIv, sent));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public static ArrayList<Msg> getMsgsPending(String username) {

        ArrayList<Msg> messages = new ArrayList<>();

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM msgs WHERE msg_receiver = ? AND sent = FALSE");
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int msgId = rs.getInt("msg_id");
                String message = rs.getString("msg");
                String send = rs.getString("msg_sender");
                String receive = rs.getString("msg_receiver");
                String aesSender = rs.getString("aes_sender");
                String aesReceiver = rs.getString("aes_receiver");
                String aesIv = rs.getString("aes_iv");
                long time = rs.getLong("time_stamp");
                boolean sent = rs.getBoolean("sent");

                messages.add(new Msg(msgId, message, send, receive, time, aesSender, aesReceiver, aesIv, sent));
            }
            for (Msg msg : messages) {
                updateSentStatus(msg.getMsgId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public static void updateSentStatus(int msg_id) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE msgs SET sent = TRUE WHERE msg_id = ?");
            ps.setInt(1, msg_id);

            int rows = ps.executeUpdate();
            System.out.println("Changed: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();

        }
    }
}
