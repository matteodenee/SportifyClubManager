package com.sportify.manager.dao;

import com.sportify.manager.services.MatchRequest;
import com.sportify.manager.services.MatchRequestStatus;
import java.sql.SQLException;
import java.util.List;

public interface MatchRequestDAO {
    MatchRequest create(MatchRequest request) throws SQLException;
    MatchRequest getById(int id) throws SQLException;
    List<MatchRequest> getPending() throws SQLException;
    List<MatchRequest> getByClub(int clubId) throws SQLException;
    void updateStatus(int id, MatchRequestStatus status, Integer matchId) throws SQLException;
}
