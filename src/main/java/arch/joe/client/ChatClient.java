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

import arch.joe.app.User;
import arch.joe.security.Crypto;

public class ChatClient extends WebSocketClient {

    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

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
        System.out.println(message);
        messageQueue.add(message);
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

    public void loginRequest(String user, String pass) {
        JsonObject request = new JsonObject();
        request.addProperty("type", "login");
        request.addProperty("username", user);
        request.addProperty("password", pass);

        send(request.toString());
    }

    public void saltRequest(String user) {
        JsonObject request = new JsonObject();
        request.addProperty("type", "salt_request");
        request.addProperty("username", user);
        send(request.toString());
    }

    public String waitForMessage() throws InterruptedException {
        return messageQueue.take();
    }

    // type: login
    // authorized: t or f
    // token: token or none
    public static void main(String[] args) throws Exception {

        String token;

        ChatClient c = new ChatClient(new URI(
                "ws://localhost:8025/text-me/chat"));
        c.connect();
        Thread.sleep(100);
        c.saltRequest("Youssef");
        String salt = c.waitForMessage();

        c.loginRequest("Youssef", Crypto.stringToHash("password", salt));
        String login = c.waitForMessage();

        JsonElement jsonElement = new JsonParser().parse(login);
        JsonObject obj = jsonElement.getAsJsonObject();
        String type = obj.get("authorized").getAsString();

        if (type.equals("f")) {
            System.out.println("Wrong Password");
        } else {
            token = obj.get("token").getAsString();
        }
    }
}
