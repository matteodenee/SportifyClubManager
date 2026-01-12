package com.sportify.manager.communication.network;

import com.lloseng.ocsf.server.ConnectionToClient;
import com.lloseng.ocsf.server.OriginatorMessage;
import com.sportify.manager.services.ConversationService;
import com.sportify.manager.dao.PostgresUserDAO;
import com.sportify.manager.services.NetConversation;
import com.sportify.manager.services.NetMessage;

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
        if (!(arg instanceof OriginatorMessage om)) {
            return;
        }

        ConnectionToClient client = om.getOriginator();
        Object msg = om.getMessage();
        if (client == null) {
            return;
        }

        try {
            if (msg instanceof LoginRequest req) {
                String userId = req.userId();
                int clubId = PostgresUserDAO.getInstance().getClubIdByMember(userId);
                if (clubId <= 0) {
                    clubId = PostgresUserDAO.getInstance().getClubIdByDirector(userId);
                }
                if (clubId <= 0) {
                    safeSend(client, new ErrorEvent("Utilisateur sans club."));
                    return;
                }
                client.setInfo("userId", userId);
                client.setInfo("clubId", clubId);
                System.out.println("[CHAT] LoginRequest reçu pour " + userId);

                long globalId = service.onUserEnterChat(userId, clubId);

                List<NetConversation> conversations = service.listUserConversations(userId, clubId);
                client.sendToClient(new ConversationListEvent(conversations));

                List<NetMessage> hist = service.history(userId, globalId, 200);
                client.sendToClient(new HistoryEvent(globalId, hist));
                return;
            }

            String userId = (String) client.getInfo("userId");
            Integer clubId = (Integer) client.getInfo("clubId");
            if (userId == null || clubId == null) {
                safeSend(client, new ErrorEvent("Connexion non identifiée (LoginRequest requis)."));
                System.err.println("[CHAT] Message reçu avant LoginRequest: " + msg.getClass().getSimpleName());
                return;
            }

            if (msg instanceof SendMessageRequest req) {
                NetMessage saved = service.sendMessage(userId, req.conversationId(), req.content());
                server.sendToAll(new NewMessageEvent(saved));
                return;
            }

            if (msg instanceof CreateGroupRequest req) {
                String directorId = PostgresUserDAO.getInstance().getDirectorIdByClub(clubId);
                if (directorId == null || !directorId.equals(userId)) {
                    safeSend(client, new ErrorEvent("Seul le directeur peut créer un groupe."));
                    return;
                }
                service.createGroup(userId, req.groupName(), clubId, req.memberIds());
                broadcastConversationLists();
                return;
            }

            if (msg instanceof JoinConversationRequest req) {
                service.joinConversation(userId, req.conversationId(), clubId);
                client.sendToClient(new ConversationListEvent(service.listUserConversations(userId, clubId)));
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
            System.err.println("[CHAT] Erreur serveur: " + ex.getMessage());
            safeSend(client, new ErrorEvent("Erreur serveur: " + ex.getMessage()));
        }
    }

    private void safeSend(ConnectionToClient c, Object event) {
        try {
            c.sendToClient(event);
        } catch (IOException ignored) {}
    }

    private void broadcastConversationLists() {
        for (ConnectionToClient c : server.getClientConnections()) {
            try {
                String uid = (String) c.getInfo("userId");
                Integer clubId = (Integer) c.getInfo("clubId");
                if (uid == null || clubId == null) {
                    continue;
                }
                c.sendToClient(new ConversationListEvent(service.listUserConversations(uid, clubId)));
            } catch (IOException ignored) {}
        }
    }
}
