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
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.Receiver;

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
    private static HashMap<String, String> seshIDToName = new HashMap<>();
    // username : session
    private static HashMap<String, Session> nameToSesh = new HashMap<>();

    private void addToClients(Session session, String username) {
        seshIDToName.put(session.getId(), username);
        nameToSesh.put(username, session);
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("connection opened by: " + session.getId());

    }

    @OnMessage
    public void onMsg(Session session, String msg) throws Exception {
        JsonElement jsonElement = new JsonParser().parse(msg);
        JsonObject obj = jsonElement.getAsJsonObject();
        String type = obj.get("type").getAsString();

        // type:
        // username:
        // password:
        switch (type) {

            case "login" -> loginRequest(session, obj);
            case "salt_request" -> saltRequest(session, obj);
            case "send_msg" -> MsgRequest(session, obj);
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
        response.addProperty("type", "login");

        // type: login
        // authorized: t or f
        // token: token or none
        if (correctPass.equals(password)) {

            addToClients(session, username);
            response.addProperty("authorized", "t");
            response.addProperty("token", Auth.makeToken(username));

        } else {
            response.addProperty("authorized", "f");
            response.addProperty("token", "none");

        }

        System.out.println("sending: " + response);
        session.getAsyncRemote().sendText(response.toString());
    }

    public void saltRequest(Session session, JsonObject obj) {

        String username = obj.get("username").getAsString();
        User usr = Database.getUser(username);

        JsonObject response = new JsonObject();
        response.addProperty("type", "salt");

        if (usr == null) {
            System.out.println("Username not found");
            response.addProperty("salt", "none");
            System.out.println("sending: " + "none");
            session.getAsyncRemote().sendText(response.toString());

        } else {
            String salt = usr.getSalt();
            response.addProperty("salt", salt);

            System.out.println("sending: " + salt);
            session.getAsyncRemote().sendText(response.toString());
        }
    }

    // request.addProperty("message", message);
    // request.addProperty("sender", sender);
    // request.addProperty("receiver", receiver);
    // request.addProperty("time", time);
    public void MsgRequest(Session sesh, JsonObject obj) {

        String token = obj.get("token").getAsString();
        String tokenName = Auth.verifyToken(token); // also verifies the token

        if (tokenName == null) {
            System.err.println("bad token");

        } else {
            Msg msg = new Msg(obj.get("message").getAsString(),
                    obj.get("sender").getAsString(),
                    obj.get("receiver").getAsString());

            String sender = msg.getMsgSender();
            String client = seshIDToName.get(sesh.getId());

            if (!(sender.equals(tokenName) && client.equals(tokenName))) {
                System.out.println("bad sender or client");
            } else {
                System.out.println("sender is the token user and client is the proper client");
                User dbReceiver = Database.getUser(msg.getMsgReceiver());

                if (dbReceiver == null) {
                    System.out.println("user Not found");
                } else {
                    Database.insertMsg(msg);
                    Session receiverOnline = nameToSesh.get(msg.getMsgReceiver());

                    if (receiverOnline == null) {
                        System.out.println("user is not online");

                    } else {
                        System.out.println("Sending message to receiver");
                        obj.addProperty("time", msg.msgTime());
                        receiverOnline.getAsyncRemote().sendText(obj.toString());
                        System.out.println(seshIDToName);
                    }
                }
            }
        }
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
