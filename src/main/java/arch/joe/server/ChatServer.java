package arch.joe.server;

import java.io.IOException;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.glassfish.tyrus.server.Server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.app.User;
import arch.joe.db.Database;
import arch.joe.security.Auth;

// JsonObject request = new JsonObject();
// request.addProperty("type", "salt_request");
// request.addProperty("username", user);
// send(request.toString());

@ServerEndpoint(value = "/chat")
public class ChatServer {

    // session id : username
    private static HashMap<String, String> activeClients = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("connection opened by: " + session.getId());

    }

    @OnMessage
    public void onMsg(Session session, String msg) throws Exception {
        System.out.println("msg received: " + msg);
        JsonElement jsonElement = new JsonParser().parse(msg);
        JsonObject obj = jsonElement.getAsJsonObject();
        String type = obj.get("type").getAsString();

        // type:
        // username:
        // password:
        switch (type) {

            case "login" -> loginRequest(session, obj);
            case "salt_request" -> saltRequest(session, obj);
            case "register" -> System.out.println("Register type");
            default -> System.out.println("no type");

        }
    }

    public void loginRequest(Session session, JsonObject obj) {
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();

        User correctData = Database.getUser(username);
        String correctPass = correctData.getPassword();

        JsonObject response = new JsonObject();

        // type: login
        // authorized: t or f
        // token: token or none
        if (correctPass.equals(password)) {

            activeClients.put(session.getId(), username);
            response.addProperty("type", "login");
            response.addProperty("authorized", "t");
            response.addProperty("token", Auth.makeToken(username));

        } else {
            response.addProperty("type", "login");
            response.addProperty("authorized", "f");
            response.addProperty("token", "none");

        }

        System.out.println("sending: " + response);
        session.getAsyncRemote().sendText(response.toString());
    }

    public boolean checkUser(String token, Session activeSesh) {

        String tokenUsername = Auth.verifyToken(token);
        String realSeshName = activeClients.get(activeSesh.getId());

        if (realSeshName.equals(tokenUsername)) {
            return true;
        } else {
            return false;
        }
    }

    public void saltRequest(Session session, JsonObject obj) {
        String username = obj.get("username").getAsString();
        String salt = Database.getUser(username).getSalt();
        System.out.println("sending: " + salt);
        session.getAsyncRemote().sendText(salt);
    }

    @OnClose
    public void onClose(Session session) throws IOException {

        System.out.println("connection closed by: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable e) {

        System.out.println("connection fucked for: " + session.getId());
        e.printStackTrace();
    }

    public static void runServer() {
        Server server = new Server("localhost", 8025, "/text-me", null, ChatServer.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please press a key to stop the server.");
            reader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }

    public static void main(String[] args) {
        System.out.println("Server Starting...");
        runServer();
    }
}
