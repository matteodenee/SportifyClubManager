package com.sportify.manager.facade;

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

    /**
     * Use Case 3 & 9 : Récupère les données brutes de performance
     */
    public List<Statistique> getTeamStats(int teamId, String period) throws SQLException {
        return statManager.calculateTeamPerformance(teamId, period);
    }

    /**
     * Use Case 9.2.1 : Récupère les données agrégées pour les PIE CHARTS
     * (Distribution des buts, fautes, etc.)
     */
    public Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException {
        return statManager.getAggregatedStats(teamId, period);
    }

    /**
     * Use Case 5 & 9.2.1 : Ranks Performance (Top Players)
     * Pour les BAR CHARTS
     */
    public Map<String, Integer> getTopPerformers(int teamId, String eventType, int limit) throws SQLException {
        return statManager.getRanking(teamId, eventType, limit);
    }

    /**
     * Use Case 9.2.1 : Identifies Trends
     * Pour les LINE CHARTS (Évolution temporelle)
     */
    public Map<String, Integer> getTrendData(int teamId, String eventType, String start, String end) throws SQLException {
        return statManager.getTrends(teamId, eventType, start, end);
    }
}