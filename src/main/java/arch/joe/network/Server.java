package arch.joe.network;

import jakarta.websocket.*;

import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/echo")
public class Server {
    private static final Set<Session> clients = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        clients.add(session);
        System.out.println("new connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String msg) {

        try {
            System.out.println("Received: " + msg);
            for (Session client : clients) {
                if (client.isOpen() && !client.equals(session)) {
                    client.getBasicRemote().sendText(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable e) {
        System.err.println("Something went wrong. (endpoint)");
        e.printStackTrace();
    }
}
