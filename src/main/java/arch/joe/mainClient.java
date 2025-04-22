package arch.joe;

import java.net.URI;
import java.security.KeyPair;
import java.util.Scanner;

import com.google.gson.JsonObject;

import arch.joe.app.Msg;
import arch.joe.client.ChatClient;
import arch.joe.client.ChatListener;
import arch.joe.security.Crypto;

public class mainClient {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        ChatClient c = new ChatClient(new URI(
                "ws://localhost:8025/text-me/chat"));
        c.connect();
        Thread.sleep(100);

        while (true) {

            System.out.print("Login, register, change password, quit (1,2,3,0): ");
            int input = scanner.nextInt();
            scanner.nextLine();

            switch (input) {
                case 1 -> login(scanner, c);
                case 2 -> register(scanner, c);
                case 3 -> changePass(scanner, c);
                case 0 -> {
                    disconnect(c);
                }
                default -> System.err.println("choose a valid option");

            }
        }
    }

    private static void disconnect(ChatClient c) {
        c.close();
        System.exit(0);
    }

    private static void changePass(Scanner scanner, ChatClient c) throws Exception {

        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Old password: ");
        String oldPass = scanner.nextLine();
        System.out.print("New password: ");
        String newPass = scanner.nextLine();

        if (!c.changePassword(name, oldPass, newPass)) {
            System.err.println("Wrong password");

        } else {
            System.out.println("Password changed successfully");
        }
    }

    private static void register(Scanner scanner, ChatClient c) throws Exception {
        System.out.println("Register");
        System.out.println("--------");
        System.out.print("email: ");
        String email = scanner.nextLine();
        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        String salt = Crypto.makeSalt();
        KeyPair keyPair = Crypto.makeKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();

        boolean response = c.registerRequest(name, email, Crypto.stringToHash(pass, salt), salt, publicKey);

        if (!response) {
            System.err.println("username or email unavailable");

        } else {
            System.out.println("registered");
            c.setUsername(name);
            c.savePrivateKey(keyPair.getPrivate(), name);

        }
    }

    private static void login(Scanner scanner, ChatClient c) throws Exception {
        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        JsonObject obj = c.login(name, pass);

        if (obj == null) {
            System.out.println("Bad login.");

        } else {
            message(scanner, c, name, obj);

        }
    }

    private static void message(Scanner scanner, ChatClient c, String name, JsonObject obj)
            throws Exception {
        c.setToken(obj.get("token").getAsString());
        System.out.print("Enter username of the person you want to text: ");
        String receiver = scanner.nextLine();

        boolean isThere = c.userThere(receiver);

        if (!isThere) {
            System.out.println("user not found");

        } else {

            c.msgHistory(name, receiver);
            Thread thread = new Thread(new ChatListener(c));
            thread.start();
            String message;
            Thread.sleep(100);
            System.out.println("press q and anytime to quit the chat.");

            while (true) {
                message = scanner.nextLine();

                if (message.equalsIgnoreCase("q")) {
                    System.out.println("goodbye!");
                    disconnect(c);

                } else {
                    Msg msg = new Msg(message, name, receiver);
                    c.sendMsg(msg, c.getToken());

                }
            }
        }
    }
}
