package arch.joe;

import java.net.URI;
import java.util.Scanner;

import arch.joe.app.Msg;
import arch.joe.app.User;
import arch.joe.client.ChatClient;
import arch.joe.db.Database;
import arch.joe.security.Auth;
import arch.joe.security.Crypto;

public class Main {
    public static void main(String[] args) throws Exception {

        String salt = Crypto.makeSalt();
        String password = Crypto.stringToHash("password", salt);

        User usr = new User("Youssef", password, salt);

        Database.insertUsr(usr);

        // Scanner scanner = new Scanner(System.in);
        //
        // System.out.print("Username: ");
        // String name = scanner.nextLine();
        // System.out.print("Password: ");
        // String pass = scanner.nextLine();
        //
        // ChatClient c = new ChatClient(new URI(
        // "ws://localhost:8025/text-me/chat"));
        // c.connect();
        //
        // c.saltRequest("");
        //
        // c.loginRequest(name, pass);
        //
        // scanner.close();
    }
}
