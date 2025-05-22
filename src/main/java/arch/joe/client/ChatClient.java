// TODO: 
// Add token to a file

package arch.joe.client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Map;
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
import arch.joe.client.GUI.Utils;
import arch.joe.client.GUI.Messenger.Components.ChatBubble;
import arch.joe.security.Crypto;

public class ChatClient extends WebSocketClient {

    private final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<String> chatQueue = new LinkedBlockingQueue<>();
    private String token;
    private String username;
    private String currentReceiver;

    public ChatClient(URI serverUri, Draft draft) throws Exception {
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
        JsonObject obj = parseJson(response);
        response = obj.get("successful").getAsString();

        if (response.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    // 0 correct password
    // -1 wrong passwrod
    // -2 username not found
    public int login(String name, String unhashedPass) throws Exception {

        String salt = this.getSalt(name);

        if (salt.equals("none")) {
            System.out.println("username not found.");
            return -2;

        } else {
            JsonObject obj = this.loginHelper(name, Crypto.stringToHash(unhashedPass, salt));
            if (obj == null) {
                return -1;

            } else {
                this.username = name;
                this.setToken(obj.get("token").getAsString());
                return 0;
            }
        }
    }

    private JsonObject loginHelper(String name, String hashedPass) throws Exception {
        JsonObject request = new JsonObject();
        request.addProperty("type", "login");
        request.addProperty("username", name);
        request.addProperty("password", hashedPass);

        send(request.toString());
        String response = waitForMessage();

        JsonObject obj = parseJson(response);
        String auth = obj.get("authorized").getAsString();

        if (auth.equals("f")) {
            return null;
        } else {
            return obj;
        }
    }

    private String getSalt(String name) throws InterruptedException {
        JsonObject request = new JsonObject();
        request.addProperty("type", "salt_request");
        request.addProperty("username", name);
        send(request.toString());

        String saltJson = waitForMessage();

        JsonObject obj = parseJson(saltJson);
        String salt = obj.get("salt").getAsString();

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
        String response = waitForMessage();

        JsonObject obj = parseJson(response);
        String successful = obj.get("successful").getAsString();

        if (successful.equals("f")) {
            return false;

        } else {
            return true;

        }
    }

    public boolean userThere(String name) throws InterruptedException {
        JsonObject request = new JsonObject();
        request.addProperty("type", "user_there");
        request.addProperty("username", name);
        send(request.toString());

        String isThere = waitForMessage();

        JsonObject obj = parseJson(isThere);
        isThere = obj.get("is_there").getAsString();

        if (isThere.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    // failed_msg
    // user_not_found
    // user_not_online
    // user_online
    public boolean sendMsg(String message) throws Exception {

        PublicKey senderKey = getPubKey(username);
        PublicKey receiverKey = getPubKey(currentReceiver);

        SecretKey aesKey = Crypto.makeAESKey();
        String aesIv = Crypto.generateIVBytes();

        String aesSender = Crypto.cipherRSA(aesKey.getEncoded(), senderKey);
        String aesReceiver = Crypto.cipherRSA(aesKey.getEncoded(), receiverKey);

        if (senderKey == null || receiverKey == null) {
            System.err.println("Key not found");
            return false;

        } else {

            message = Crypto.cipherAES(message, aesKey, aesIv);

            JsonObject msgRequest = new JsonObject();
            msgRequest.addProperty("type", "send_msg");
            msgRequest.addProperty("message", message);
            msgRequest.addProperty("sender", username);
            msgRequest.addProperty("receiver", currentReceiver);
            msgRequest.addProperty("aes_sender", aesSender);
            msgRequest.addProperty("aes_receiver", aesReceiver);
            msgRequest.addProperty("aes_iv", aesIv);
            msgRequest.addProperty("token", token);

            send(msgRequest.toString());

            String response = waitForMessage();
            JsonObject obj = parseJson(response);
            String type = obj.get("type").getAsString();

            if (type.equals("failed_msg")) {
                System.err.println("problem with token, relog to get a new token");
                return false;

            } else if (type.equals("user_not_found")) {
                System.err.println("problem with token, relog to get a new token");
                return false;

            } else if (type.equals("user_not_online")) {
                // could use later to show if user is online or not by returning int instead of
                // boolean
                return true;

            } else { // user_online
                // could use later to show if user is online or not by returning int instead of
                // boolean
                return true;

            }
        }
    }

    private PublicKey getPubKey(String name) throws Exception {

        JsonObject reqPubKey = new JsonObject();
        reqPubKey.addProperty("type", "request_pub_key");
        reqPubKey.addProperty("username", name);

        send(reqPubKey.toString());
        String response = waitForMessage();

        JsonObject obj = parseJson(response);
        String stringKey = obj.get("key").getAsString();

        if (stringKey.equals("none")) {
            System.err.println("Key not found");
            return null;

        } else {
            byte[] keyBytes = Crypto.decoder(stringKey);
            return Crypto.bytesToPublicKey(keyBytes);

        }
    }

    public void savePrivateKey(PrivateKey key, String username) {
        Path privateKeyPath = Utils.TEXT_ME_PATH.resolve(username + "_private.key");

        try (FileOutputStream out = new FileOutputStream(privateKeyPath.toFile())) {
            out.write(key.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Something went wrong with the writing of the file");
        }
    }

    public PrivateKey readPrivateKey(String username) throws Exception {
        Path privateKeyPath = Utils.TEXT_ME_PATH.resolve(username + "_private.key");

        try (FileInputStream in = new FileInputStream(privateKeyPath.toFile())) {

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

    public ArrayList<Msg> msgHistory() throws Exception {

        Gson gson = new Gson();

        JsonObject historyRequest = new JsonObject();
        historyRequest.addProperty("type", "history_request");
        historyRequest.addProperty("sender", this.username);
        historyRequest.addProperty("receiver", currentReceiver);

        send(historyRequest.toString());
        String response = waitForMessage();
        JsonObject obj = parseJson(response);

        JsonArray jsonArray = obj.getAsJsonArray("msgs");
        ArrayList<Msg> msgsList = new ArrayList<>();

        System.out.println("------------");
        System.out.println("    Chat    ");
        System.out.println("------------");

        for (JsonElement elem : jsonArray) {
            Msg msg = gson.fromJson(elem, Msg.class);
            String sender = msg.getMsgSender();
            PrivateKey rsaKey = readPrivateKey(getUsername());
            String decryptedMsg = decipherMsg(msg, rsaKey);

            if (decryptedMsg != null) {
                System.out.println(sender + " (history): " + decryptedMsg);
                msg.setMsg(decryptedMsg);
            }

            msgsList.add(msg);
        }

        return msgsList;
    }

    public void getPending() throws Exception {
        Gson gson = new Gson();

        JsonObject pendingMsgReq = new JsonObject();
        pendingMsgReq.addProperty("type", "pending_request");
        pendingMsgReq.addProperty("username", this.username);

        send(pendingMsgReq.toString());

        String response = waitForMessage();
        JsonObject obj = parseJson(response);

        if (obj.get("type").getAsString().equals("pending_empty")) {
            return;

        } else {
            JsonArray jsonArray = obj.getAsJsonArray("msgs");

            for (JsonElement elem : jsonArray) {
                Msg msg = gson.fromJson(elem, Msg.class);
                PrivateKey rsaKey = readPrivateKey(getUsername());
                String decryptedMsg = decipherMsg(msg, rsaKey);

                if (decryptedMsg != null) {
                    msg.setMsg(decryptedMsg);
                    Utils.mController.addMsgUi(new ChatBubble(msg.getMsg(), false, Utils.timeFormat(msg.msgTime())),
                            msg);
                } else {
                    System.err.println("Decryption failed.");
                }
            }
        }
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

    private JsonObject parseJson(String response) {
        JsonElement elem = JsonParser.parseString(response);
        return elem.getAsJsonObject();
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

    public void setCurrentReceiver(String receiver) {
        this.currentReceiver = receiver;

    }

    public String getCurrentReceiver() {
        return currentReceiver;
    }
}
