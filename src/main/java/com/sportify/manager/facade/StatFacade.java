package com.sportify.manager.facade;

import com.sportify.manager.services.SmallEvent;
import com.sportify.manager.services.StatManager;
import com.sportify.manager.services.Statistique;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class StatFacade {
    private StatManager statManager;

    public StatFacade() {
        this.statManager = new StatManager();
    }


    public List<Statistique> getTeamStats(int teamId, String period) throws SQLException {
        return statManager.calculateTeamPerformance(teamId, period);
    }


    public Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException {
        return statManager.getAggregatedStats(teamId, period);
    }

    public List<SmallEvent> getMatchEvents(int matchId) throws SQLException {
        return statManager.getMatchEvents(matchId);
    }


    public Map<String, Integer> getTopPerformers(int teamId, String eventType, int limit) throws SQLException {
        return statManager.getRanking(teamId, eventType, limit);
    }


    public Map<String, Integer> getTrendData(int teamId, String eventType, String start, String end) throws SQLException {
        return statManager.getTrends(teamId, eventType, start, end);
    }
}
