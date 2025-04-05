package arch.joe;

import java.net.URI;
import java.util.Scanner;

import com.google.gson.JsonObject;

import arch.joe.app.Msg;
import arch.joe.client.ChatClient;
import arch.joe.client.ChatListener;

public class mainClient {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();

        ChatClient c = new ChatClient(new URI(
                "ws://localhost:8025/text-me/chat"));
        JsonObject obj = c.login(name, pass);

        if (obj == null) {
            System.out.println("Bad login.");
            System.exit(0);
        } else {
            c.setToken(obj.get("token").getAsString());
            System.out.print("Enter username of the person you want to text: ");
            String receiver = scanner.nextLine();

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

        scanner.close();
    }
}
