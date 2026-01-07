package com.sportify.manager.services;

import com.sportify.manager.dao.StatDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatManager {

    private StatDAO getDAO() {
        return AbstractFactory.getFactory().createStatDAO();
    }

    /**
     * USE CASE 3 & 9 : Calcul détaillé de la performance
     * Version enrichie de ta méthode initiale
     */
    public List<Statistique> calculateTeamPerformance(int teamId, String period) throws SQLException {
        List<SmallEvent> events = getDAO().getEventsByTeam(teamId, period);
        List<Statistique> results = new ArrayList<>();

        // Calculs via Stream (comme tu avais commencé)
        long buts = events.stream().filter(e -> "GOAL".equalsIgnoreCase(e.getType())).count();
        long matchs = events.stream().filter(e -> "MATCH".equalsIgnoreCase(e.getType())).count();

        results.add(new Statistique("Total Buts", (double) buts, period, "buts"));

        // 9.2.1 Step 4 : Calculates averages and ratios
        if (matchs > 0) {
            double moyenneButs = (double) buts / matchs;
            results.add(new Statistique("Moyenne Buts/Match", moyenneButs, period, "ratio"));
        }

        return results;
    }

    /**
     * USE CASE 9.2.1 : Aggregated Data for Charts
     * Récupère la répartition (ex: pour un PieChart)
     */
    public Map<String, Integer> getAggregatedStats(int teamId, String period) throws SQLException {
        return getDAO().getAggregatedStatsByTeam(teamId, period);
    }

    /**
     * USE CASE 5 & 9.2.1 : Ranks Performance
     * Pour identifier les meilleurs joueurs d'une équipe (ex: pour un BarChart)
     */
    public Map<String, Integer> getRanking(int teamId, String eventType, int limit) throws SQLException {
        return getDAO().getTopPerformers(teamId, eventType, limit);
    }

    /**
     * USE CASE 9.2.1 : Identifies Trends
     * Récupère les données temporelles (ex: pour un LineChart)
     */
    public Map<String, Integer> getTrends(int teamId, String eventType, String start, String end) throws SQLException {
        return getDAO().getTrendData(teamId, eventType, start, end);
    }
}