package arch.joe.client;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.app.Msg;
import arch.joe.security.Crypto;

public class ChatClient extends WebSocketClient {

    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> chatQueue = new LinkedBlockingQueue<>();
    private String token = new String();

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

        JsonElement jsonElement = new JsonParser().parse(message);
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
        // if the error is fatal then onClose will be called additionally
    }

    public void registerRequest(String name, String hashedPass, String salt) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("type", "register");
        request.addProperty("username", name);
        request.addProperty("password", hashedPass);
        request.addProperty("salt", salt);

        send(request.toString());
    }

    public JsonObject login(String name, String hashedPass) throws Exception {

        this.saltRequest(name);
        String saltJson = this.waitForMessage();

        JsonElement saltElement = new JsonParser().parse(saltJson);
        JsonObject saltObj = saltElement.getAsJsonObject();
        String salt = saltObj.get("salt").getAsString();

        if (salt.equals("none")) {
            System.out.println("username not found.");
            return null;

        } else {
            this.loginRequest(name, Crypto.stringToHash(hashedPass, salt));
            String login = this.waitForMessage();

            JsonElement jsonElement = new JsonParser().parse(login);
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

    public void userThere(String name) {
        JsonObject request = new JsonObject();
        request.addProperty("type", "user_there");
        request.addProperty("username", name);
        send(request.toString());
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
