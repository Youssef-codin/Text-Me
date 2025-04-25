package arch.joe.client;

import java.security.PrivateKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.app.Msg;

public class ChatListener implements Runnable {

    private boolean is_running = true;
    private static ChatClient c;

    public ChatListener(ChatClient chatClient) {
        c = chatClient;
    }

    @Override
    public void run() {

        while (is_running) {

            try {
                String chatMsg = c.waitForChat();
                printMessage(chatMsg);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void printMessage(String jsonMessage) throws Exception {

        JsonElement jsonElement = JsonParser.parseString(jsonMessage);
        JsonObject message = jsonElement.getAsJsonObject();

        String type = message.get("type").getAsString();
        Msg msg = new Msg(
                message.get("message").getAsString(),
                message.get("sender").getAsString(),
                message.get("receiver").getAsString(),
                message.get("aes_sender").getAsString(),
                message.get("aes_receiver").getAsString(),
                message.get("aes_iv").getAsString());

        if (type.equals("bad_token")) {
            System.err.println("BAD TOKEN");

        } else {
            PrivateKey key = c.readPrivateKey(c.getUsername());
            System.out.println(c.decipherMsg(msg, key));

        }
    }

    public void stopListener() {
        is_running = false;

    }
}
