package arch.joe.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.app.Msg;
import arch.joe.security.Crypto;

public class ChatClient extends WebSocketClient {

    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> chatQueue = new LinkedBlockingQueue<>();
    private String token;

    public ChatClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public ChatClient(URI serverURI) {
        super(serverURI);
    }

    public ChatClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload:
        // onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        // System.out.println("Client received: " + message + " @ " +
        // System.identityHashCode(message));

        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject obj = jsonElement.getAsJsonObject();
        String type = obj.get("type").getAsString();

        if (type.equals("receive_msg")) {
            chatQueue.add(message);
        } else {
            messageQueue.add(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        System.exit(0);
        // if the error is fatal then onClose will be called additionally
    }

    public boolean registerRequest(String name, String hashedPass, String salt) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("type", "register");
        request.addProperty("username", name);
        request.addProperty("password", hashedPass);
        request.addProperty("salt", salt);

        send(request.toString());

        String response = waitForMessage();
        JsonElement jsonElement = JsonParser.parseString(response);
        JsonObject obj = jsonElement.getAsJsonObject();
        response = obj.get("successful").getAsString();
        if (response.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public JsonObject login(String name, String hashedPass) throws Exception {

        this.saltRequest(name);
        String saltJson = this.waitForMessage();

        JsonElement saltElement = JsonParser.parseString(saltJson);
        JsonObject saltObj = saltElement.getAsJsonObject();
        String salt = saltObj.get("salt").getAsString();

        if (salt.equals("none")) {
            System.out.println("username not found.");
            return null;

        } else {
            this.loginRequest(name, Crypto.stringToHash(hashedPass, salt));
            String login = this.waitForMessage();

            JsonElement jsonElement = JsonParser.parseString(login);
            JsonObject obj = jsonElement.getAsJsonObject();

            String auth = obj.get("authorized").getAsString();

            if (auth.equals("f")) {
                return null;
            } else {
                return obj;
            }
        }
    }

    private void loginRequest(String name, String pass) {
        JsonObject request = new JsonObject();
        request.addProperty("type", "login");
        request.addProperty("username", name);
        request.addProperty("password", pass);

        send(request.toString());
    }

    public void saltRequest(String name) {
        JsonObject request = new JsonObject();
        request.addProperty("type", "salt_request");
        request.addProperty("username", name);
        send(request.toString());
    }

    public boolean userThere(String name) throws InterruptedException {
        JsonObject request = new JsonObject();
        request.addProperty("type", "user_there");
        request.addProperty("username", name);
        send(request.toString());

        String isThere = waitForMessage();

        JsonElement jsonElement = JsonParser.parseString(isThere);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        isThere = jsonObject.get("is_there").getAsString();

        if (isThere.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    // msg
    // msgSender
    // msgReceiver
    // timeStamp
    public void sendMsg(Msg msg, String token) {
        String message = msg.getMsg();
        String sender = msg.getMsgSender();
        String receiver = msg.getMsgReceiver();

        JsonObject msgRequest = new JsonObject();
        msgRequest.addProperty("type", "send_msg");
        msgRequest.addProperty("message", message);
        msgRequest.addProperty("sender", sender);
        msgRequest.addProperty("receiver", receiver);
        msgRequest.addProperty("token", token);

        send(msgRequest.toString());
    }

    public ArrayList<Msg> msgHistory(String name1, String name2) throws InterruptedException {

        Gson gson = new Gson();

        JsonObject historyRequest = new JsonObject();
        historyRequest.addProperty("type", "history_request");
        historyRequest.addProperty("name1", name1);
        historyRequest.addProperty("name2", name2);

        send(historyRequest.toString());
        String response = waitForMessage();
        JsonElement element = JsonParser.parseString(response);
        JsonObject obj = element.getAsJsonObject();

        JsonArray jsonArray = obj.getAsJsonArray("msgs");
        ArrayList<Msg> msgsList = new ArrayList<>();

        System.out.println("------------");
        System.out.println("    Chat    ");
        System.out.println("------------");

        for (JsonElement elem : jsonArray) {
            Msg msg = gson.fromJson(elem, Msg.class);
            msgsList.add(msg);
            String sender = msg.getMsgSender();
            String messageText = msg.getMsg();

            System.out.println(sender + ": " + messageText);
        }

        return msgsList;
    }

    public String waitForMessage() throws InterruptedException {
        return messageQueue.take();
    }

    public String waitForChat() throws InterruptedException {
        return chatQueue.take();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
