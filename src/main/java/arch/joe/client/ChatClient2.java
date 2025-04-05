package arch.joe.client;

import java.net.URI;

import com.google.gson.JsonObject;

public class ChatClient2 {

    public static void main(String[] args) throws Exception {

        String token;
        URI serverURI = new URI("ws://localhost:8025/text-me/chat");

        ChatClient c = new ChatClient(serverURI);

        JsonObject obj = c.login("Joe", "password");

        if (obj == null) {
            System.out.println("Wrong Password");
        } else {
            token = obj.get("token").getAsString();

            Thread thread = new Thread(new ChatListener(c));
            thread.start();
        }
    }
}
