/*
 * TO DO make update user safer
 */

package arch.joe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import arch.joe.app.Contact;
import arch.joe.app.User;

public class UserDao {

    private UserDao() {

    }

    public static boolean insertUser(User usr) {
        String name = usr.getName();
        String pass = usr.getPassword();
        String email = usr.getEmail();
        String salt = usr.getSalt();
        byte[] key = usr.getKey();

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement(
                            "INSERT INTO users(usr_name, usr_email, usr_password, usr_salt, usr_key) values(?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, salt);
            ps.setBytes(5, key);

            int rows = ps.executeUpdate();
            System.out.println("Changed: " + rows);
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                System.err.println("Username or email is already taken.");
                return false;
            } else {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static User getUser(String name) throws Exception {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE usr_name = ?");
            ps.setString(1, name);
            ResultSet results = ps.executeQuery();

            if (!results.next()) {
                System.out.println("Username not found.");
                return null;
            } else {
                return new User(results.getString("usr_name"),
                        results.getString("usr_email"),
                        results.getString("usr_password"),
                        results.getString("usr_salt"),
                        results.getBytes("usr_key"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // need to make this safer
    // public static void updateUser(String column, String newThing, String name) {
    // try (Connection conn = Database.connect()) {
    // PreparedStatement ps = conn.prepareStatement("UPDATE users SET " + column + "
    // = ? WHERE usr_name = ?");
    // ps.setString(1, newThing);
    // ps.setString(2, name);
    //
    // int rows = ps.executeUpdate();
    // System.out.println("Changed: " + rows);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }

    public static ArrayList<Contact> searchUser(String name) {
        ArrayList<Contact> list = new ArrayList<>();

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE usr_name LIKE ?");
            ps.setString(1, name + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Contact(rs.getString("usr_name")));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void changePassword(String name, String newHashedPassword) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET usr_password = ? WHERE usr_name = ?");
            ps.setString(1, newHashedPassword);
            ps.setString(2, name);

            int rows = ps.executeUpdate();
            System.out.println("Changed: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(String name) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE usr_name = ?");
            ps.setString(1, name);
            int rows = ps.executeUpdate();
            System.out.println("Changed: " + rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
