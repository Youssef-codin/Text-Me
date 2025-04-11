package arch.joe.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import arch.joe.app.User;

public class UserDao {

    private UserDao() {

    }

    public static boolean insertUser(User usr) {
        String name = usr.getName();
        String pass = usr.getPassword();
        String salt = usr.getSalt();

        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn
                    .prepareStatement("INSERT INTO users(usr_name, usr_password, usr_salt) values(?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, pass);
            ps.setString(3, salt);

            int columns = ps.executeUpdate();
            System.out.println("Changed: " + columns);
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                System.out.println(name + " is already taken.");
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
                return new User(results.getString("usr_name"), results.getString("usr_password"),
                        results.getString("usr_salt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateUser(String column, String newThing, String name) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE users SET " + column + " = ? WHERE usr_name = ?");
            ps.setString(1, newThing);
            ps.setString(2, name);
            int columns = ps.executeUpdate();
            System.out.println("Changed: " + columns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(String name) {
        try (Connection conn = Database.connect()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE usr_name = ?");
            ps.setString(1, name);
            int columns = ps.executeUpdate();
            System.out.println("Changed: " + columns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
