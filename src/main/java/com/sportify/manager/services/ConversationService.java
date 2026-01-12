package com.sportify.manager.services;

import com.sportify.manager.dao.ConversationDAO;
import com.sportify.manager.dao.MessageDAO;
import com.sportify.manager.dao.PostgresUserDAO;

import java.time.Instant;
import java.util.List;

public class ConversationService {

    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final PostgresUserDAO userDAO = PostgresUserDAO.getInstance();

    public ConversationService(ConversationDAO conversationDAO, MessageDAO messageDAO) {
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
    }

    public long onUserEnterChat(String userId, int clubId) {
        validateClubAccess(userId, clubId);
        long globalId = conversationDAO.ensureGlobalConversation(clubId);
        if (globalId <= 0) {
            throw new IllegalStateException("Impossible d'initialiser GLOBAL.");
        }
        conversationDAO.addParticipant(globalId, userId);
        return globalId;
    }

    public List<NetConversation> listUserConversations(String userId, int clubId) {
        validateClubAccess(userId, clubId);
        return conversationDAO.listUserConversations(userId, clubId);
    }

    public List<NetMessage> history(String userId, long conversationId, int limit) {
        if (!conversationDAO.isParticipant(conversationId, userId)) {
            throw new IllegalArgumentException("Accès refusé (pas membre).");
        }
        try {
            return messageDAO.getHistory(conversationId, limit);
        } catch (java.sql.SQLException e) {
            throw new IllegalStateException("Erreur récupération historique: " + e.getMessage(), e);
        }
    }

    public long createGroup(String userId, String groupName, int clubId, List<String> memberIds) {
        validateClubAccess(userId, clubId);
        long id = conversationDAO.createGroup(groupName, userId, clubId);
        if (id <= 0) {
            throw new IllegalStateException("Création groupe impossible.");
        }
        conversationDAO.addParticipant(id, userId);
        if (memberIds != null) {
            for (String memberId : memberIds) {
                if (memberId != null && userDAO.isMemberInClub(memberId, clubId)) {
                    conversationDAO.addParticipant(id, memberId);
                }
            }
        }
        String directorId = userDAO.getDirectorIdByClub(clubId);
        if (directorId != null && !directorId.isBlank()) {
            conversationDAO.addParticipant(id, directorId);
        }
        return id;
    }

    public void joinConversation(String userId, long conversationId, int clubId) {
        validateClubAccess(userId, clubId);
        NetConversation conv = conversationDAO.getConversationById(conversationId, clubId);
        if (conv == null) {
            throw new IllegalArgumentException("Conversation introuvable.");
        }
        conversationDAO.addParticipant(conversationId, userId);
    }

    public NetMessage sendMessage(String userId, long conversationId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message vide interdit.");
        }
        if (!conversationDAO.isParticipant(conversationId, userId)) {
            throw new IllegalArgumentException("Vous n'êtes pas membre de cette conversation.");
        }
        NetMessage msg = new NetMessage(conversationId, userId, content.trim(), Instant.now());
        try {
            messageDAO.save(msg);
        } catch (java.sql.SQLException e) {
            throw new IllegalStateException("Erreur enregistrement message: " + e.getMessage(), e);
        }
        return msg;
    }

    private void validateClubAccess(String userId, int clubId) {
        if (clubId <= 0) {
            throw new IllegalArgumentException("Club invalide.");
        }
        if (userDAO.isMemberInClub(userId, clubId)) {
            return;
        }
        int managedClubId = userDAO.getClubIdByDirector(userId);
        if (managedClubId == clubId) {
            return;
        }
        if (!userDAO.isMemberInClub(userId, clubId)) {
            throw new IllegalArgumentException("Utilisateur non membre du club.");
        }
    }
}
