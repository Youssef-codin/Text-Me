package arch.joe;

import java.net.URI;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

            System.out.print("Login or register (1,2): ");
            int input = scanner.nextInt();
            scanner.nextLine();

            if (input == 1) {
                login(scanner, c);

            } else {
                register(scanner, c);
            }
        }
    }

    private static void register(Scanner scanner, ChatClient c) throws Exception, InterruptedException {
        System.out.println("Register");
        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        String salt = Crypto.makeSalt();

        c.registerRequest(name, Crypto.stringToHash(pass, salt), salt);
        String response = c.waitForMessage();
        JsonElement jsonElement = new JsonParser().parse(response);
        JsonObject obj = jsonElement.getAsJsonObject();
        response = obj.get("successful").getAsString();

        if (response.equals("true")) {
            System.out.println("registered");
        } else {
            System.err.println("username unavailable");
        }
    }

    private static void login(Scanner scanner, ChatClient c) throws Exception, InterruptedException {
        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        JsonObject obj = c.login(name, pass);

        if (obj == null) {
            System.out.println("Bad login.");
            System.exit(0);

        } else {
            c.setToken(obj.get("token").getAsString());
            System.out.print("Enter username of the person you want to text: ");
            String receiver = scanner.nextLine();

            c.userThere(receiver);
            String isThere = c.waitForMessage();

            JsonElement jsonElement = new JsonParser().parse(isThere);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            isThere = jsonObject.get("is_there").getAsString();

            if (isThere.equals("false")) {
                System.out.println("user not found");

            } else {

                Thread thread = new Thread(new ChatListener(c));
                thread.start();
                String message;
                Thread.sleep(100);
                System.out.println("press q and anytime to quit the chat.");

                while (true) {
                    message = scanner.nextLine();

                    if (message.equalsIgnoreCase("q")) {
                        System.out.println("goodbye!");
                        System.exit(0);

                    } else {
                        Msg msg = new Msg(message, name, receiver);
                        c.sendMsg(msg, c.getToken());

                    }
                }

            }
        }
    }
}
