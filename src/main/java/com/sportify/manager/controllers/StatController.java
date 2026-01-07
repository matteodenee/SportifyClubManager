package com.sportify.manager.controllers;

import com.sportify.manager.facade.StatFacade;
import com.sportify.manager.services.SmallEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class StatController {
    private StatFacade statFacade;

    public StatController() {
        this.statFacade = new StatFacade();
    }

    /**
     * USE CASE 3 & 9 : Récupère les stats agrégées pour un camembert (PieChart)
     * Répartit par type (Buts, Fautes, Arrêts...)
     */
    public Map<String, Integer> getTeamDistribution(int teamId, String period) {
        try {
            // Appel à la méthode agrégée que nous avons ajoutée au DAO/Facade
            return statFacade.getAggregatedStatsByTeam(teamId, period);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * USE CASE 9.2.1 step 4 : Calcule les ratios et pourcentages
     * Exemple : % de victoires ou ratio buts/match
     */
    public Map<String, Double> getPerformanceRatios(int teamId, String period) {
        Map<String, Double> ratios = new HashMap<>();
        try {
            Map<String, Integer> rawData = statFacade.getAggregatedStatsByTeam(teamId, period);

            int totalMatchs = rawData.getOrDefault("MATCH_JOUE", 0);
            int victoires = rawData.getOrDefault("VICTOIRE", 0);
            int buts = rawData.getOrDefault("BUT", 0);

            if (totalMatchs > 0) {
                // Calcul du pourcentage de victoire (Basic Flow 9.2.1)
                ratios.put("WinRate", (double) victoires / totalMatchs * 100);
                // Calcul de la moyenne de buts (Basic Flow 9.2.1)
                ratios.put("GoalsPerMatch", (double) buts / totalMatchs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratios;
    }

    /**
     * USE CASE 5 : Rank Performance (Top Players)
     * Pour un graphique en barres (BarChart)
     */
    public Map<String, Integer> getTopScorers(int teamId) {
        try {
            return statFacade.getTopPerformers(teamId, "BUT", 5); // Top 5 buteurs
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * USE CASE 8 : Compare Statistics
     * Permet de comparer deux équipes
     */
    public Map<String, Map<String, Integer>> compareTeams(int id1, int id2, String period) {
        Map<String, Map<String, Integer>> comparison = new HashMap<>();
        comparison.put("TeamA", getTeamDistribution(id1, period));
        comparison.put("TeamB", getTeamDistribution(id2, period));
        return comparison;
    }
}