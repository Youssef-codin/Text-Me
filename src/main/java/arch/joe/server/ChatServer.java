//work on encrypting AES keys

package arch.joe.server;

import java.io.IOException;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.tyrus.server.Server;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.HOTPGenerator;
import com.bastiaanjansen.otp.SecretGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import arch.joe.app.Msg;
import arch.joe.app.User;
import arch.joe.db.MessageDao;
import arch.joe.db.UserDao;
import arch.joe.security.Auth;
import arch.joe.security.Crypto;

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

    private void removeSeshAndName(Session sesh, String name) {
        if (sesh == null) {
            return;
        }

        seshIDToName.remove(sesh.getId());
        if (name != null) {
            nameToSesh.remove(name);

        }
    }

    @OnOpen
    public void onOpen(Session sesh) throws IOException {
        System.out.println("connection opened by: " + sesh.getId());

    }

    // type:
    // username:
    // password:
    @OnMessage
    public void onMsg(Session sesh, String msg) throws Exception {

        if (msg.length() > 10_000) {
            System.err.println("Msg too big");
            sesh.close(new CloseReason(CloseCodes.TOO_BIG, "Message too big."));
            return;

        }

        JsonElement jsonElement = JsonParser.parseString(msg);

        if (!jsonElement.isJsonObject()) {
            System.err.println("Incorrect msg formatting");
            sesh.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Incorrectly formatted message"));
            return;

        }

        JsonObject obj = jsonElement.getAsJsonObject();

        if (!obj.has("type")) {
            System.err.println("Incorrect msg formatting");
            sesh.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "Incorrectly formatted message"));
            return;

        }

        String type = obj.get("type").getAsString();
        switch (type) {

            case "login" -> loginRequest(sesh, obj);
            case "salt_request" -> saltRequest(sesh, obj);
            case "send_msg" -> msgRequest(sesh, obj);
            case "register" -> registerRequest(sesh, obj);
            case "user_there" -> userThere(sesh, obj);
            case "history_request" -> historyRequest(sesh, obj);
            case "request_pub_key" -> keyRequest(sesh, obj);
            case "change_password" -> changePass(sesh, obj);
            default -> {
                sesh.close(new CloseReason(CloseCodes.CANNOT_ACCEPT, "'Type' is empty"));
                System.err.println("no type");
            }
        }
    }

    @OnClose
    public void onClose(Session sesh) throws IOException {
        System.out.println("connection closed by: " + sesh.getId());
        String name = seshIDToName.get(sesh.getId());
        removeSeshAndName(sesh, name);
    }

    @OnError
    public void onError(Session sesh, Throwable e) {

        System.err.println("connection fucked for: " + sesh.getId());
        String name = seshIDToName.get(sesh.getId());
        removeSeshAndName(sesh, name);
        e.printStackTrace();
    }

    // type: register
    // username: name
    // password: hashedPass
    // salt: salt
    // email: email
    private void registerRequest(Session sesh, JsonObject obj) throws Exception {

        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();
        String email = obj.get("email").getAsString();
        String salt = obj.get("salt").getAsString();
        String stringKey = obj.get("key").getAsString();
        byte[] key = Crypto.decoder(stringKey);

        JsonObject response = new JsonObject();
        response.addProperty("type", "register_request");

        boolean validEmail = isValidEmailAddress(email);

        if (!validEmail) {
            System.err.println("invalid Email");
            response.addProperty("successful", "false");

        } else {

            User usr = new User(username, email, password, salt, key);
            boolean available = UserDao.insertUser(usr);

            if (!available) {
                System.err.println("username or email unavailable");
                response.addProperty("successful", "false");

            } else {
                System.out.println("username available");
                response.addProperty("successful", "true");

            }
        }

        sesh.getAsyncRemote().sendText(response.toString());
    }

    private static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private void loginRequest(Session sesh, JsonObject obj) throws Exception {
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();

        User correctData = UserDao.getUser(username);
        String correctPass = correctData.getPassword();

        JsonObject response = new JsonObject();
        response.addProperty("type", "login");

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

    private boolean checkPass(String username, String password) throws Exception {

        User correctData = UserDao.getUser(username);
        String correctPass = correctData.getPassword();

        if (!correctPass.equals(password)) {
            return false;

        } else {
            return true;

        }
    }

    private void saltRequest(Session sesh, JsonObject obj) throws Exception {

        String username = obj.get("username").getAsString();
        User usr = UserDao.getUser(username);

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

    private void userThere(Session sesh, JsonObject obj) throws Exception {
        String username = obj.get("username").getAsString();

        User available = UserDao.getUser(username);

        JsonObject response = new JsonObject();
        response.addProperty("type", "user_there");

        if (available == null) {
            response.addProperty("is_there", "false");

        } else {
            response.addProperty("is_there", "true");

        }

        sesh.getAsyncRemote().sendText(response.toString());
    }

    private int checkToken(Session sesh, JsonObject obj, String sender) {

        String token = obj.get("token").getAsString();
        String tokenName = Auth.verifyToken(token);
        String client = seshIDToName.get(sesh.getId());

        if (tokenName == null) {
            return -1;

        } else if (!sender.equals(tokenName)) {
            System.out.println("token name: " + tokenName);
            System.out.println("sender name: " + sender);
            return -2;

        } else if (!client.equals(tokenName)) {
            return -3;

        } else {
            return 0;

        }
    }

    private void changePass(Session sesh, JsonObject obj) throws Exception {
        String username = obj.get("username").getAsString();
        String oldPass = obj.get("old_password").getAsString();
        String newPass = obj.get("new_password").getAsString();

        boolean check = checkPass(username, oldPass);

        JsonObject response = new JsonObject();
        response.addProperty("type", "change_password");

        if (!check) {
            response.addProperty("successful", "f");

        } else {
            UserDao.changePassword(username, newPass);
            response.addProperty("successful", "t");

        }

        sesh.getAsyncRemote().sendText(response.toString());
    }

    // message: message
    // sender: sender
    // receiver: receiver
    // time: time
    private void msgRequest(Session sesh, JsonObject obj) throws Exception {

        Msg msg = new Msg(
                obj.get("message").getAsString(),
                obj.get("sender").getAsString(),
                obj.get("receiver").getAsString(),
                obj.get("aes_sender").getAsString(),
                obj.get("aes_receiver").getAsString(),
                obj.get("aes_iv").getAsString());

        String sender = msg.getMsgSender();
        int tokenCheck = checkToken(sesh, obj, sender);

        if (tokenCheck == -1) {
            System.err.println("bad token");
            JsonObject badToken = new JsonObject();
            badToken.addProperty("type", "bad_token");
            sesh.getAsyncRemote().sendText(badToken.toString());

        } else if (tokenCheck == -2) {
            System.err.println("sender is not the owner of that token");

        } else if (tokenCheck == -3) {
            System.err.println("sessionID does not match the token owner");

        } else {
            msgResponse(obj, msg);

        }
    }

    private void msgResponse(JsonObject obj, Msg msg) throws Exception {
        System.out.println("sender is the token user and client is the proper client");
        User dbReceiver = UserDao.getUser(msg.getMsgReceiver());
        System.out.println(dbReceiver);

        if (dbReceiver == null) {
            System.err.println("user Not found");

        } else {
            MessageDao.insertMsg(msg);
            Session receiverOnline = nameToSesh.get(msg.getMsgReceiver());

            for (HashMap.Entry<String, Session> name : nameToSesh.entrySet()) {
                System.out.println(name.getKey() + " -> " + name.getValue().getId());
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

    private void historyRequest(Session sesh, JsonObject obj) {

        Gson gson = new Gson();

        String name1 = obj.get("name1").getAsString();
        String name2 = obj.get("name2").getAsString();

        ArrayList<Msg> msgs = MessageDao.getMsgs(name1, name2);
        JsonArray msgArray = new JsonArray();

        for (Msg msg : msgs) {
            JsonElement elem = gson.toJsonTree(msg);
            msgArray.add(elem);
        }

        JsonObject response = new JsonObject();
        response.addProperty("type", "convo");
        response.add("msgs", msgArray);

        sesh.getAsyncRemote().sendText(response.toString());
    }

    private void keyRequest(Session sesh, JsonObject obj) throws Exception {
        String name = obj.get("username").getAsString();
        JsonObject response = new JsonObject();
        response.addProperty("type", "key");

        User user = UserDao.getUser(name);
        if (user == null) {
            response.addProperty("key", "none");
            System.err.println("user not found");

        } else {
            response.addProperty("key", Crypto.encoder(user.getKey()));

        }
        sesh.getAsyncRemote().sendText(response.toString());
    }

    public static void runServer() {
        Server server = new Server("localhost", 8025, "/text-me", null, ChatServer.class);
        Scanner scanner = new Scanner(System.in);

        try {
            server.start();
            System.out.print("Press a key to stop the server");
            scanner.nextLine();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            server.stop();
            scanner.close();
        }
    }

    public static void main(String[] args) {
        System.out.println("Server Starting...");
        runServer();
    }
}
