package arch.joe.db;

import arch.joe.app.User;
import arch.joe.security.Crypto;

public class DBtesting {
    public static void main(String[] args) {

        Database.connect();
        // String salt = Crypto.makeSalt();
        // try {
        // Database.insertUsr(new User("Ahmed132", Crypto.stringToHash("password",
        // salt), salt));
        // Database.insertUsr(new User("Mohamed", Crypto.stringToHash("password", salt),
        // salt));
        // Database.insertUsr(new User("Youssef", Crypto.stringToHash("password", salt),
        // salt));
        // Database.insertUsr(new User("Ali", Crypto.stringToHash("password", salt),
        // salt));
        // Database.insertUsr(new User("Seif", Crypto.stringToHash("password", salt),
        // salt));
        // Database.insertUsr(new User("Wowa123i2u31", Crypto.stringToHash("password",
        // salt), salt));
        // Database.insertUsr(new User("AnnoyingOrange", Crypto.stringToHash("password",
        // salt), salt));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        Database.showAllTable("users");
    }
}
