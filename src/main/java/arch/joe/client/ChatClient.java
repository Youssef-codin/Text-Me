// TODO: 
// whenever a token expires, send a msg that makes the person relogin
// Add token to a file

package arch.joe.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
    private String username;
    private String currentReceiver;

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

            String sender = obj.get("sender").getAsString();

            if (this.currentReceiver != null) {
                if (this.currentReceiver.equals(sender)) {
                    chatQueue.add(message);
                }
            }

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
        this.close();
        // if the error is fatal then onClose will be called additionally
    }

    public boolean registerRequest(String name, String email, String hashedPass, String salt, byte[] pubKey)
            throws Exception {

        JsonObject request = new JsonObject();
        request.addProperty("type", "register");
        request.addProperty("username", name);
        request.addProperty("email", email);
        request.addProperty("password", hashedPass);
        request.addProperty("salt", salt);
        request.addProperty("key", Crypto.encoder(pubKey));

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

        String salt = this.getSalt(name);

        if (salt.equals("none")) {
            System.out.println("username not found.");
            return null;

        } else {
            JsonObject obj = this.checkPassword(name, Crypto.stringToHash(hashedPass, salt));

            if (obj == null) {
                return null;

            } else {
                this.username = name;
                return obj;
            }
        }
    }

    private String getSalt(String name) throws InterruptedException {
        JsonObject request = new JsonObject();
        request.addProperty("type", "salt_request");
        request.addProperty("username", name);
        send(request.toString());

        String saltJson = this.waitForMessage();

        JsonElement saltElement = JsonParser.parseString(saltJson);
        JsonObject saltObj = saltElement.getAsJsonObject();
        String salt = saltObj.get("salt").getAsString();

        return salt;
    }

    public boolean changePassword(String name, String oldPass, String newPass) throws Exception {

        String salt = getSalt(name);

        JsonObject request = new JsonObject();
        request.addProperty("type", "change_password");
        request.addProperty("username", name);
        request.addProperty("old_password", Crypto.stringToHash(oldPass, salt));
        request.addProperty("new_password", Crypto.stringToHash(newPass, salt));

        send(request.toString());
        String response = this.waitForMessage();

        JsonElement elem = JsonParser.parseString(response);
        JsonObject object = elem.getAsJsonObject();
        String successful = object.get("successful").getAsString();

        if (successful.equals("f")) {
            return false;

        } else {
            return true;

        }
    }

    private JsonObject checkPassword(String name, String hashedPass) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("type", "login");
        request.addProperty("username", name);
        request.addProperty("password", hashedPass);

        send(request.toString());
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

    public void sendMsg(String message, String sender, String receiver, String token) throws Exception {

        PublicKey senderKey = getPubKey(sender);
        PublicKey receiverKey = getPubKey(receiver);

        SecretKey aesKey = Crypto.makeAESKey();
        String aesIv = Crypto.generateIVBytes();

        String aesSender = Crypto.cipherRSA(aesKey.getEncoded(), senderKey);
        String aesReceiver = Crypto.cipherRSA(aesKey.getEncoded(), receiverKey);

        if (senderKey == null || receiverKey == null) {
            System.err.println("Key not found");

        } else {

            message = Crypto.cipherAES(message, aesKey, aesIv);

            JsonObject msgRequest = new JsonObject();
            msgRequest.addProperty("type", "send_msg");
            msgRequest.addProperty("message", message);
            msgRequest.addProperty("sender", sender);
            msgRequest.addProperty("receiver", receiver);
            msgRequest.addProperty("aes_sender", aesSender);
            msgRequest.addProperty("aes_receiver", aesReceiver);
            msgRequest.addProperty("aes_iv", aesIv);
            msgRequest.addProperty("token", token);

            // debug
            // System.out.println(msgRequest.toString());
            send(msgRequest.toString());

        }
    }

    private PublicKey getPubKey(String name) throws Exception {

        JsonObject reqPubKey = new JsonObject();
        reqPubKey.addProperty("type", "request_pub_key");
        reqPubKey.addProperty("username", name);

        send(reqPubKey.toString());
        String response = waitForMessage();

        JsonElement elem = JsonParser.parseString(response);
        JsonObject object = elem.getAsJsonObject();
        String stringKey = object.get("key").getAsString();

        if (stringKey.equals("none")) {
            System.err.println("Key not found");
            return null;

        } else {
            byte[] keyBytes = Crypto.decoder(stringKey);
            return Crypto.bytesToPublicKey(keyBytes);

        }
    }

    public void savePrivateKey(PrivateKey key, String username) {

        try (FileOutputStream out = new FileOutputStream(username + "_private.key")) {
            out.write(key.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong with the writing of the file");
        }
    }

    public PrivateKey readPrivateKey(String username) throws Exception {

        try (FileInputStream in = new FileInputStream(username + "_private.key")) {

            byte[] readBytes = in.readAllBytes();

            KeyFactory factory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(readBytes);
            PrivateKey key = factory.generatePrivate(privKeySpec);
            return key;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong with the reading of the file");
            return null;
        }
    }

    public ArrayList<Msg> msgHistory(String name1, String name2) throws Exception {

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
            PrivateKey rsaKey = readPrivateKey(getUsername());
            String decryptedMsg = decipherMsg(msg, rsaKey);

            if (decryptedMsg != null) {
                System.out.println(sender + " (history): " + decryptedMsg);
            }
        }

        return msgsList;
    }

    protected String decipherMsg(Msg msg, PrivateKey rsaKey) throws Exception {

        if (rsaKey != null) {

            String rsaEncryptedAes;
            String aesText = msg.getMsg();

            if (msg.getMsgSender().equals(username)) {
                rsaEncryptedAes = msg.getAesSender();

            } else {
                rsaEncryptedAes = msg.getAesReceiver();

            }

            byte[] aesKeyBytes = Crypto.decipherRSA(rsaEncryptedAes, rsaKey);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            return Crypto.decipherAES(aesText, aesKey, msg.getAesIv());
        }

        return null;
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

    public String getUsername() {
        return this.username;

    }

    public void setUsername(String username) {
        this.username = username;

    }

    public void setCurrentReceiver(String receiver) {
        this.currentReceiver = receiver;

    }

    public String getCurrentReceiver() {
        return currentReceiver;
    }
}
