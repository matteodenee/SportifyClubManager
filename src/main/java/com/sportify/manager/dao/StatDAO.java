package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StatDAO {


    List<SmallEvent> getEventsByTeam(int teamId, String period) throws SQLException;

    List<SmallEvent> getEventsByPlayer(String playerId, String period) throws SQLException;

    List<SmallEvent> getEventsByMatch(int matchId) throws SQLException;

    void addSmallEvent(SmallEvent event) throws SQLException;


    Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException;


    Map<String, Double> getPlayerPerformanceMetrics(String playerId, String period) throws SQLException;


    Map<String, Integer> getTopPerformers(int teamId, String eventType, int limit) throws SQLException;


    Map<String, Integer> getTrendData(int teamId, String eventType, String startDate, String endDate) throws SQLException;
}
