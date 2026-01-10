import communication.network.*;
import communication.network.ChatClientObserver.NetworkEventSink;
import com.sportify.manager.services.UserManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class CommunicationFacade implements NetworkEventSink {

    private static CommunicationFacade instance;

    private final List<CommunicationListener> listeners = new CopyOnWriteArrayList<>();
    private ChatClient client;

    private volatile long selectedConversationId = -1;

    private CommunicationFacade() {}

    public static CommunicationFacade getInstance() {
        if (instance == null) instance = new CommunicationFacade();
        return instance;
    }

    public void addListener(CommunicationListener l) { if (l != null) listeners.add(l); }
    public void removeListener(CommunicationListener l) { listeners.remove(l); }

    public void connect(String host, int port) {
        var user = UserManager.createUserManager().getCurrentUser();
        if (user == null) {
            fireError("Aucun utilisateur connecté (ouvrez l'app via le login).");
            return;
        }

        try {
            client = new ChatClient(host, port);
            client.addObserver(new ChatClientObserver(this));
            client.open();

            // ✅ login réseau automatique (user déjà log dans l'app)
            client.send(new LoginRequest(user.getId()));
        } catch (IOException ex) {
            fireError("Serveur chat indisponible: " + ex.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) client.close();
        } catch (IOException ignored) {}
    }

    public void selectConversation(long conversationId) {
        selectedConversationId = conversationId;
        try {
            if (client != null && client.isConnected()) {
                client.send(new GetHistoryRequest(conversationId, 200));
            }
        } catch (IOException ex) {
            fireError("Erreur demande historique: " + ex.getMessage());
        }
    }

    public void sendMessage(String text) {
        if (selectedConversationId <= 0) {
            fireError("Aucune conversation sélectionnée.");
            return;
        }
        try {
            client.send(new SendMessageRequest(selectedConversationId, text));
        } catch (IOException ex) {
            fireError("Erreur envoi: " + ex.getMessage());
        }
    }

    public void createGroup(String name) {
        try {
            client.send(new CreateGroupRequest(name));
        } catch (IOException ex) {
            fireError("Erreur création groupe: " + ex.getMessage());
        }
    }

    public void joinConversation(long conversationId) {
        try {
            client.send(new JoinConversationRequest(conversationId));
        } catch (IOException ex) {
            fireError("Erreur rejoindre: " + ex.getMessage());
        }
    }

    // === NetworkEventSink : appelé via Platform.runLater dans ChatClientObserver ===
    @Override
    public void onNetworkObject(Object msg) {
        if (msg instanceof ConversationListEvent e) {
            for (var l : listeners) l.onConversations(e.conversations());
        } else if (msg instanceof HistoryEvent e) {
            for (var l : listeners) l.onHistory(e.conversationId(), e.history());
        } else if (msg instanceof NewMessageEvent e) {
            for (var l : listeners) l.onNewMessage(e.message());
        } else if (msg instanceof ErrorEvent e) {
            fireError(e.message());
        } else if (msg instanceof String s) {
            for (var l : listeners) l.onSystem(s);
        } else if (msg instanceof Exception ex) {
            fireError("Erreur réseau: " + ex.getMessage());
        } else {
            fireError("Message inconnu du serveur: " + msg);
        }
    }

    private void fireError(String msg) {
        for (var l : listeners) l.onError(msg);
    }
}
