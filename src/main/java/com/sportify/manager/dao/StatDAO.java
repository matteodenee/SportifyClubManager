package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface StatDAO {

    // --- RÉCUPÉRATION BRUTE (Use Case 9.2.1 - Match Data) ---

    List<SmallEvent> getEventsByTeam(int teamId, String period) throws SQLException;

    List<SmallEvent> getEventsByPlayer(String playerId, String period) throws SQLException;

    void addSmallEvent(SmallEvent event) throws SQLException;

    // --- RÉCUPÉRATION AGRÉGÉE (Use Case 9.2.1 - Calculated Statistics) ---

    /**
     * Calcule les totaux par type d'événement (ex: Buts, Fautes, Victoires).
     * Utile pour les PIE CHARTS (Use Case 2 : View Statistics).
     */
    Map<String, Integer> getAggregatedStatsByTeam(int teamId, String period) throws SQLException;

    /**
     * Calcule les statistiques de performance d'un joueur (ex: Ratio buts/match).
     * (Use Case 5 : Generate Player Statistics).
     */
    Map<String, Double> getPlayerPerformanceMetrics(String playerId, String period) throws SQLException;

    /**
     * Classe les meilleures performances au sein d'une équipe ou d'un club.
     * (Use Case 9.2.1 - Ranks Performance).
     */
    Map<String, Integer> getTopPerformers(int teamId, String eventType, int limit) throws SQLException;

    /**
     * Récupère les données historiques pour identifier des tendances.
     * (Use Case 9.2.1 - Identifies Trends).
     * Retourne une Map : Date -> Valeur
     */
    Map<String, Integer> getTrendData(int teamId, String eventType, String startDate, String endDate) throws SQLException;
}