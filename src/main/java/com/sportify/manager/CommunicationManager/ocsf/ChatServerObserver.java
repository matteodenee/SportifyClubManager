package communication.network;

import com.lloseng.ocsf.server.ConnectionToClient;
import com.lloseng.ocsf.server.OriginatorMessage;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public final class ChatServerObserver implements Observer {

    private final ChatServer server;
    private final ConversationService service;

    public ChatServerObserver(ChatServer server, ConversationService service) {
        this.server = server;
        this.service = service;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof OriginatorMessage om)) return;

        ConnectionToClient client = om.getOriginator();
        Object msg = om.getMessage();
        if (client == null) return;

        try {
            // 1) Identification réseau
            if (msg instanceof LoginRequest req) {
                String userId = req.userId();
                client.setInfo("userId", userId);

                long globalId = service.onUserEnterChat(userId);

                List<NetConversation> conversations = service.listUserConversations(userId);
                client.sendToClient(new ConversationListEvent(conversations));

                List<NetMessage> hist = service.history(userId, globalId, 200);
                client.sendToClient(new HistoryEvent(globalId, hist));
                return;
            }

            // 2) Toutes les autres requêtes exigent userId
            String userId = (String) client.getInfo("userId");
            if (userId == null) {
                safeSend(client, new ErrorEvent("Connexion non identifiée (LoginRequest requis)."));
                client.close();
                return;
            }

            if (msg instanceof SendMessageRequest req) {
                NetMessage saved = service.sendMessage(userId, req.conversationId(), req.content());
                server.sendToAll(new NewMessageEvent(saved));
                return;
            }

            if (msg instanceof CreateGroupRequest req) {
                service.createGroup(userId, req.groupName());
                client.sendToClient(new ConversationListEvent(service.listUserConversations(userId)));
                return;
            }

            if (msg instanceof JoinConversationRequest req) {
                service.joinConversation(userId, req.conversationId());
                client.sendToClient(new ConversationListEvent(service.listUserConversations(userId)));
                List<NetMessage> hist = service.history(userId, req.conversationId(), 200);
                client.sendToClient(new HistoryEvent(req.conversationId(), hist));
                return;
            }

            if (msg instanceof GetHistoryRequest req) {
                List<NetMessage> hist = service.history(userId, req.conversationId(), req.limit());
                client.sendToClient(new HistoryEvent(req.conversationId(), hist));
                return;
            }

            safeSend(client, new ErrorEvent("Requête inconnue: " + msg.getClass().getSimpleName()));

        } catch (IllegalArgumentException ex) {
            safeSend(client, new ErrorEvent(ex.getMessage()));
        } catch (Exception ex) {
            safeSend(client, new ErrorEvent("Erreur serveur: " + ex.getMessage()));
        }
    }

    private void safeSend(ConnectionToClient c, Object event) {
        try { c.sendToClient(event); } catch (IOException ignored) {}
    }
}
