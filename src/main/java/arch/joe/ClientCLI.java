package arch.joe;

import java.net.URI;
import java.security.KeyPair;
import java.util.Scanner;

import arch.joe.client.ChatClient;
import arch.joe.client.ChatListener;
import arch.joe.security.Crypto;

public class ClientCLI {

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

        int response = c.login(name, pass);

        if (response == -1 || response == -2) {
            System.out.println("Bad login.");

        } else {
            message(scanner, c, name);

        }
    }

    private static void message(Scanner scanner, ChatClient c, String name)
            throws Exception {
        System.out.print("Enter username of the person you want to text: ");
        String receiver = scanner.nextLine();

        boolean isThere = c.userThere(receiver);

        if (!isThere) {
            System.out.println("user not found");

        } else {

            System.out.println("send q at anytime to quit the chat.");
            c.setCurrentReceiver(receiver);
            c.msgHistory();
            Thread thread = new Thread(new ChatListener(c));
            thread.start();
            String message;
            Thread.sleep(100);

            while (true) {
                message = scanner.nextLine();

                if (message.equalsIgnoreCase("q")) {
                    System.out.println("goodbye!");
                    disconnect(c);

                } else {
                    boolean sent = c.sendMsg(message, c.getUsername(), receiver, c.getToken());
                    if (!sent) {
                        break;
                    }
                }
            }

            login(scanner, c);
        }
    }
}
