package com.sportify.manager.dao;

import com.sportify.manager.services.NetMessage;
import java.sql.SQLException;
import java.util.List;

public interface MessageDAO {
    long save(NetMessage message) throws SQLException;
    List<NetMessage> getHistory(long conversationId, int limit) throws SQLException;
}
