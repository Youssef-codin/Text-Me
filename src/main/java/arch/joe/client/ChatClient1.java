package arch.joe.client;

import java.net.URI;

import com.google.gson.JsonObject;

import arch.joe.app.Msg;

public class ChatClient1 {

    public static void main(String[] args) throws Exception {

        Msg msg = new Msg("hi", "Youssef", "Joe");
        String token;

        ChatClient c = new ChatClient(new URI(
                "ws://localhost:8025/text-me/chat"));

        JsonObject obj = c.login("Youssef", "password");

        if (obj == null) {
            System.out.println("Wrong Password");
        } else {
            token = obj.get("token").getAsString();

            Thread thread = new Thread(new ChatListener(c));
            thread.start();

            c.sendMsg(msg, token);
            String msgResponse = c.waitForMessage();

            System.out.println(msgResponse);

        }
    }
}
