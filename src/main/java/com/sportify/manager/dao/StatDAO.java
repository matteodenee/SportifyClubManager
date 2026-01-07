package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.sql.SQLException;
import java.util.List;

public interface StatDAO {
    /**
     * Récupère tous les événements bruts d'une équipe pour une période donnée.
     */
    List<SmallEvent> getEventsByTeam(int teamId, String period) throws SQLException;

    /**
     * Récupère tous les événements bruts d'un joueur spécifique.
     */
    List<SmallEvent> getEventsByPlayer(String playerId, String period) throws SQLException;

    /**
     * Optionnel : Permet d'ajouter un nouvel événement (but, carton) en base.
     */
    void addSmallEvent(SmallEvent event) throws SQLException;
}