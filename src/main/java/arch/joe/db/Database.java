
/*
 * TO DO:
 * add more select commands to search for certain stuff
 * add modify 
 * add delete
 */

package arch.joe.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import arch.joe.app.Msg;
import arch.joe.app.User;

public abstract class Database {
    private static final String url = "jdbc:sqlite:database/chat.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Successful connection to the SQLite database.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return null;
        }
    }

    // CRUD

    // users has usr_name, usr_password, usr_salt
    // msgs has msg_id, msg_sender, msg_receiver, msg, time_stamp
    public static void showAllTable(String table) {

        table = table.toLowerCase();
        if (!table.matches("users|msgs")) {
            throw new IllegalArgumentException("Invalid place");
        } else {

            try (Connection conn = DriverManager.getConnection(url)) {

                PreparedStatement ps = conn
                        .prepareStatement("SELECT * FROM " + table);

                ResultSet rs = ps.executeQuery();

                if (table.equals("users")) {
                    System.out.printf("%-20s %-60s%n", "Username", "Password (Hashed)");
                    System.out.println("-----------------------------------------------------------------");

                    while (rs.next()) {
                        System.out.printf("%-20s %-60s%n",
                                rs.getString("usr_name"),
                                rs.getString("usr_password"));
                    }
                } else {
                    // msgs has msg_id, msg_sender, msg_receiver, msg, time_stamp
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

    // users has usr_name, usr_password, usr_salt
    public static void insertUsr(User usr) {

        String name = usr.getName();
        String pass = usr.getPassword();
        String salt = usr.getSalt();

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn
                    .prepareStatement("INSERT INTO users(usr_name, usr_password, usr_salt) values(?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, pass);
            ps.setString(3, salt);

            int columns = ps.executeUpdate();
            System.out.println("Changed: " + columns);

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {

                System.out.println(name + " is already taken.");
            } else {
                e.printStackTrace();
            }
        }
    }

    public static void deleteUser() {

    }

    // msgs has msg_id, msg_sender, msg_receiver, msg, time_stamp
    public static void insertMsg(Msg msg) {

        String sender = msg.getMsgSender();
        String receiver = msg.getMsgReceiver();
        String message = msg.getMsg();
        long time = msg.msgTime();

        try (Connection conn = DriverManager.getConnection(url)) {

            PreparedStatement ps = conn
                    .prepareStatement("INSERT INTO msgs(msg_sender, msg_receiver, msg, time_stamp) values(?, ?, ?, ?)");
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, message);
            ps.setLong(4, time);

            int columns = ps.executeUpdate();
            System.out.println("Changed: " + columns);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
