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

        try (Connection conn = Database.connect()) {
            Statement sm = conn.createStatement();
            sm.execute("PRAGMA foreign_keys = ON");

            PreparedStatement ps = conn
                    .prepareStatement(
                            "INSERT INTO msgs(msg_sender, msg_receiver, msg, time_stamp, aes_sender, aes_receiver, aes_iv) values(?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, message);
            ps.setLong(4, time);
            ps.setString(5, aesSender);
            ps.setString(6, aesReceiver);
            ps.setString(7, aesIv);

            int rows = ps.executeUpdate();
            System.out.println("Changed: " + rows);

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 19) {
                System.out.println("User does not exist.");
            }
        }
    }

    public static ArrayList<Msg> getMsgs(String user1, String user2) {

        ArrayList<Msg> messages = new ArrayList<>();

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM msgs WHERE (msg_sender = ? AND msg_receiver = ?) OR (msg_sender = ? AND msg_receiver = ?) ORDER BY time_stamp ASC");
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String message = rs.getString("msg");
                String sender = rs.getString("msg_sender");
                String receiver = rs.getString("msg_receiver");
                String aesSender = rs.getString("aes_sender");
                String aesReceiver = rs.getString("aes_receiver");
                String aesIv = rs.getString("aes_iv");
                long time = rs.getLong("time_stamp");
                messages.add(new Msg(message, sender, receiver, time, aesSender, aesReceiver, aesIv));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public static void showTable(String table) {
        table = table.toLowerCase();
        if (!table.matches("users|msgs")) {
            throw new IllegalArgumentException("Invalid table name");
        }

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = ps.executeQuery();

            if (table.equals("users")) {
                System.out.printf("%-20s %-60s%n", "Username", "Password (Hashed)");
                System.out.println("-----------------------------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-20s %-60s%n", rs.getString("usr_name"), rs.getString("usr_password"));
                }
            } else {
                while (rs.next()) {
                    System.out.println("Message ID: " + rs.getInt("msg_id"));
                    System.out.println("Message: " + rs.getString("msg"));
                    System.out.println("Sender: " + rs.getString("msg_sender"));
                    System.out.println("Receiver: " + rs.getString("msg_receiver"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
