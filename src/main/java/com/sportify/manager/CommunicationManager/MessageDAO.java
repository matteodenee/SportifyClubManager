package com.sportify.manager.CommunicationManager;

import java.sql.SQLException;
import java.util.List;

public interface MessageDAO {
    long save(Message message) throws SQLException;
    List<Message> getHistory(long conversationId, int limit) throws SQLException;
}
