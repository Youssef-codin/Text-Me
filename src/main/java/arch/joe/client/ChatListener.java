package arch.joe.client;

import java.security.PrivateKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.security.Crypto;

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

        if (type.equals("bad_token")) {
            System.err.println("BAD TOKEN");

        } else {
            String sender = message.get("sender").getAsString();
            String encryptedMessageText = message.get("message").getAsString();

            PrivateKey key = c.readPrivateKey(c.getUsername());

            if (key != null) {
                String messageText = Crypto.decipher(encryptedMessageText, key);
                System.out.println(sender + ": " + messageText);

            } else {
                // log user out and make user log back in to get a new token
            }
        }
    }

    public void stopListener() {
        is_running = false;

    }
}
