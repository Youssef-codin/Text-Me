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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.tyrus.server.Server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.app.Msg;
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
    private static Map<String, String> seshIDToName = new ConcurrentHashMap<>();
    // username : session
    private static Map<String, Session> nameToSesh = new ConcurrentHashMap<>();

    private static void addToClients(Session sesh, String username) {
        seshIDToName.put(sesh.getId(), username);
        nameToSesh.put(username, sesh);
    }

    @OnOpen
    public void onOpen(Session sesh) throws IOException {
        System.out.println("connection opened by: " + sesh.getId());

    }

    @OnMessage
    public void onMsg(Session sesh, String msg) throws Exception {
        JsonElement jsonElement = new JsonParser().parse(msg);
        JsonObject obj = jsonElement.getAsJsonObject();
        String type = obj.get("type").getAsString();

        // type:
        // username:
        // password:
        switch (type) {

            case "login" -> loginRequest(sesh, obj);
            case "salt_request" -> saltRequest(sesh, obj);
            case "send_msg" -> msgRequest(sesh, obj);
            case "register" -> registerRequest(sesh, obj);
            case "user_there" -> userThere(sesh, obj);
            default -> System.err.println("no type");
        }
    }

    @OnClose
    public void onClose(Session sesh) throws IOException {
        System.out.println("connection closed by: " + sesh.getId());
        String name = seshIDToName.get(sesh.getId());
        seshIDToName.remove(sesh.getId());
        nameToSesh.remove(name);
    }

    @OnError
    public void onError(Session sesh, Throwable e) {

        System.err.println("connection fucked for: " + sesh.getId());
        String name = seshIDToName.get(sesh.getId());
        seshIDToName.remove(sesh.getId());
        nameToSesh.remove(name);
        e.printStackTrace();
    }

    // type: register
    // username: name
    // password: hashedPass
    // salt: salt
    private void registerRequest(Session sesh, JsonObject obj) throws Exception {
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();
        String salt = obj.get("salt").getAsString();

        User usr = new User(username, password, salt);
        boolean available = Database.insertUsr(usr);
        JsonObject response = new JsonObject();
        response.addProperty("type", "register_request");

        if (!available) {
            System.err.println("username unavailable");
            response.addProperty("successful", "false");

        } else {
            System.out.println("username available");
            response.addProperty("successful", "true");

        }

        sesh.getAsyncRemote().sendText(response.toString());
    }

    private void loginRequest(Session sesh, JsonObject obj) {
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();

        User correctData = Database.getUser(username);
        String correctPass = correctData.getPassword();

        JsonObject response = new JsonObject();
        response.addProperty("type", "login");

        // type: login
        // authorized: t or f
        // token: token or none
        if (correctPass.equals(password)) {

            addToClients(sesh, username);
            response.addProperty("authorized", "t");
            response.addProperty("token", Auth.makeToken(username));

        } else {
            response.addProperty("authorized", "f");
            response.addProperty("token", "none");

        }
        System.out.println("sending: " + response);
        sesh.getAsyncRemote().sendText(response.toString());
    }

    private void saltRequest(Session sesh, JsonObject obj) {

        String username = obj.get("username").getAsString();
        User usr = Database.getUser(username);

        JsonObject response = new JsonObject();
        response.addProperty("type", "salt");

        if (usr == null) {
            System.err.println("Username not found");
            response.addProperty("salt", "none");
            System.out.println("sending: " + "none");
            sesh.getAsyncRemote().sendText(response.toString());

        } else {
            String salt = usr.getSalt();
            response.addProperty("salt", salt);

            System.out.println("sending: " + salt);
            sesh.getAsyncRemote().sendText(response.toString());
        }
    }

    private void userThere(Session sesh, JsonObject obj) {
        String username = obj.get("username").getAsString();

        User available = Database.getUser(username);

        JsonObject response = new JsonObject();
        response.addProperty("type", "user_there");

        if (available == null) {
            response.addProperty("is_there", "false");

        } else {
            response.addProperty("is_there", "true");

        }

        sesh.getAsyncRemote().sendText(response.toString());
    }

    private boolean checkToken(Session sesh, JsonObject obj, String sender) {

        String token = obj.get("token").getAsString();
        String tokenName = Auth.verifyToken(token); // also verifies the token
        String client = seshIDToName.get(sesh.getId());

        if (tokenName == null || !(sender.equals(tokenName) && client.equals(tokenName))) {
            return false;

        } else {
            return true;

        }
    }

    // message: message
    // sender: sender
    // receiver: receiver
    // time: time
    private void msgRequest(Session sesh, JsonObject obj) {

        Msg msg = new Msg(obj.get("message").getAsString(),
                obj.get("sender").getAsString(),
                obj.get("receiver").getAsString());

        String sender = msg.getMsgSender();

        if (!checkToken(sesh, obj, sender)) {
            System.err.println("bad token, sender or client");
            JsonObject badToken = new JsonObject();
            badToken.addProperty("type", "bad_token");
            sesh.getAsyncRemote().sendText(badToken.toString());
        } else {
            msgResponse(obj, msg);

        }
    }

    private void msgResponse(JsonObject obj, Msg msg) {
        System.out.println("sender is the token user and client is the proper client");
        User dbReceiver = Database.getUser(msg.getMsgReceiver());
        System.out.println(dbReceiver);

        if (dbReceiver == null) {
            System.err.println("user Not found");

        } else {
            Database.insertMsg(msg);
            Session receiverOnline = nameToSesh.get(msg.getMsgReceiver());

            for (HashMap.Entry<String, Session> name : nameToSesh.entrySet()) {
                System.out.println(name.getKey() + " => " + name.getValue().getId());
            }

            if (receiverOnline == null) {
                System.out.println("user is not online");

            } else {
                System.out.println("Sending message to receiver");
                obj.addProperty("time", msg.msgTime());
                obj.addProperty("type", "receive_msg");
                receiverOnline.getAsyncRemote().sendText(obj.toString());

            }
        }
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
