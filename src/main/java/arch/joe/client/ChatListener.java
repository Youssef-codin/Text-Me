package arch.joe.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ChatListener implements Runnable {

    private boolean is_running = true;
    ChatClient c;

    public ChatListener(ChatClient chatClient) {
        this.c = chatClient;
    }

    @Override
    public void run() {

        while (is_running) {

            try {
                String chatMsg = c.waitForChat();
                printMessage(chatMsg);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printMessage(String jsonMessage) {
        JsonElement jsonElement = JsonParser.parseString(jsonMessage);
        JsonObject message = jsonElement.getAsJsonObject();

        String type = message.get("type").getAsString();

        if (type.equals("bad_token")) {
            System.err.println("BAD TOKEN");

        } else {
            String sender = message.get("sender").getAsString();
            String messageText = message.get("message").getAsString();

            System.out.println(sender + ": " + messageText);

        }
    }

    public void stopListener() {
        is_running = false;

    }
}
