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
     * Intègre maintenant les données issues de MatchManagement
     */
    public List<Statistique> calculateTeamPerformance(int teamId, String period) throws SQLException {
        List<SmallEvent> events = getDAO().getEventsByTeam(teamId, period);
        List<Statistique> results = new ArrayList<>();

        // Comptage des types d'événements
        long buts = events.stream().filter(e -> "GOAL".equalsIgnoreCase(e.getType())).count();
        long matchs = events.stream().filter(e -> "MATCH".equalsIgnoreCase(e.getType())).count();
        long victoires = events.stream().filter(e -> "VICTOIRE".equalsIgnoreCase(e.getType())).count();

        results.add(new Statistique("Total Buts", (double) buts, period, "buts"));
        results.add(new Statistique("Victoires", (double) victoires, period, "count"));

        // 9.2.1 : Calcul des ratios
        if (matchs > 0) {
            double moyenneButs = (double) buts / matchs;
            double winRate = ((double) victoires / matchs) * 100;

            results.add(new Statistique("Moyenne Buts/Match", moyenneButs, period, "ratio"));
            results.add(new Statistique("Taux de Victoire (%)", winRate, period, "percentage"));
        }

        return results;
    }

    /**
     * NOUVEAU : Calcul du taux de présence (Lien avec CompositionManagement)
     * Utilise les événements 'PARTICIPATION' générés par le CompositionDAO
     */
    public double getPlayerParticipationRate(String playerId, int teamId, String period) throws SQLException {
        List<SmallEvent> playerEvents = getDAO().getEventsByTeam(teamId, period);

        long participations = playerEvents.stream()
                .filter(e -> playerId.equals(e.getPlayerId()) && "PARTICIPATION".equalsIgnoreCase(e.getType()))
                .count();

        long totalMatchs = playerEvents.stream()
                .filter(e -> "MATCH".equalsIgnoreCase(e.getType()))
                .count();

        return totalMatchs > 0 ? (double) participations / totalMatchs : 0.0;
    }

    /**
     * USE CASE 9.2.1 : Aggregated Data for Charts
     */
    public Map<String, Integer> getAggregatedStats(int teamId, String period) throws SQLException {
        return getDAO().getAggregatedStatsByTeam(teamId, period);
    }

    /**
     * USE CASE 5 & 9.2.1 : Ranks Performance
     */
    public Map<String, Integer> getRanking(int teamId, String eventType, int limit) throws SQLException {
        return getDAO().getTopPerformers(teamId, eventType, limit);
    }

    /**
     * USE CASE 9.2.1 : Identifies Trends
     */
    public Map<String, Integer> getTrends(int teamId, String eventType, String start, String end) throws SQLException {
        return getDAO().getTrendData(teamId, eventType, start, end);
    }
}