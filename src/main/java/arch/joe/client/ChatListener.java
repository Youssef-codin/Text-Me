package arch.joe.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatListener implements Runnable {

    private boolean is_running = true;
    ChatClient c;

    public ChatListener(ChatClient chatClient) {
        this.c = chatClient;
    }

    @Override
    public void run() {

        System.out.println("------------");
        System.out.println("    Chat    ");
        System.out.println("------------");

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
        JsonElement jsonElement = new JsonParser().parse(jsonMessage);
        JsonObject message = jsonElement.getAsJsonObject();

        String type = message.get("type").getAsString();
        String sender = message.get("sender").getAsString();
        String receiver = message.get("receiver").getAsString();
        String messageText = message.get("message").getAsString();
        String token = message.get("token").getAsString();
        long time = message.get("time").getAsLong();

        // Convert the timestamp to a human-readable format
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        Date resultdate = new java.util.Date(time);
        String timeString = sdf.format(resultdate);

        if ("send_msg".equals(type)) {
            System.out.println("[" + timeString + "] " + sender + " to " + receiver + ": " + messageText);
        }
    }

    public void stopListener() {
        is_running = false;
    }
}
