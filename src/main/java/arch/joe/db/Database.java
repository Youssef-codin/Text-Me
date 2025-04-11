package arch.joe.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private Database() {

    }

    private static final String url = "jdbc:sqlite:database/chat.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Successfully connected to the SQLite database.");
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
}
