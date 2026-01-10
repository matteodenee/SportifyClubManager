import com.sportify.manager.dao.ConversationDAO;
import com.sportify.manager.dao.MessageDAO;
import communication.network.NetConversation;
import communication.network.NetMessage;

import java.time.Instant;
import java.util.List;

public class ConversationService {

    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;

    public ConversationService(ConversationDAO conversationDAO, MessageDAO messageDAO) {
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
    }

    // appelé à la connexion réseau du chat (user déjà log dans l’app)
    public long onUserEnterChat(String userId) {
        long globalId = conversationDAO.ensureGlobalConversation();
        if (globalId <= 0) throw new IllegalStateException("Impossible d'initialiser GLOBAL.");
        conversationDAO.addParticipant(globalId, userId); // auto-join
        return globalId;
    }

    public List<NetConversation> listUserConversations(String userId) {
        return conversationDAO.listUserConversations(userId);
    }

    public List<NetMessage> history(String userId, long conversationId, int limit) {
        if (!conversationDAO.isParticipant(conversationId, userId)) {
            throw new IllegalArgumentException("Accès refusé (pas membre).");
        }
        return messageDAO.getHistory(conversationId, limit);
    }

    public long createGroup(String userId, String groupName) {
        long id = conversationDAO.createGroup(groupName, userId);
        if (id <= 0) throw new IllegalStateException("Création groupe impossible.");
        conversationDAO.addParticipant(id, userId);
        return id;
    }

    public void joinConversation(String userId, long conversationId) {
        NetConversation conv = conversationDAO.getConversationById(conversationId);
        if (conv == null) throw new IllegalArgumentException("Conversation introuvable.");
        conversationDAO.addParticipant(conversationId, userId);
    }

    public NetMessage sendMessage(String userId, long conversationId, String content) {
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Message vide interdit.");
        if (!conversationDAO.isParticipant(conversationId, userId)) {
            throw new IllegalArgumentException("Vous n'êtes pas membre de cette conversation.");
        }
        NetMessage msg = new NetMessage(conversationId, userId, content.trim(), Instant.now());
        messageDAO.save(msg);
        return msg;
    }
}
