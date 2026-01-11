package com.sportify.manager.dao;

import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;
import java.util.List;

/**
 * Interface DAO pour la gestion des équipes.
 */
public interface TeamDAO {
    
    /**
     * Crée une nouvelle équipe.
     */
    void create(Team team) throws Exception;
    
    /**
     * Récupère une équipe par son ID.
     */
    Team findById(int teamId) throws Exception;
    
    /**
     * Récupère toutes les équipes d'un club.
     */
    List<Team> findAllByClubId(int clubId) throws Exception;
    
    /**
     * Récupère toutes les équipes d'un coach.
     */
    List<Team> findAllByCoachId(String coachId) throws Exception;
    
    /**
     * Met à jour une équipe existante.
     */
    void update(Team team) throws Exception;
    
    /**
     * Supprime une équipe.
     */
    void delete(int teamId) throws Exception;
    
    /**
     * Ajoute un joueur à une équipe.
     */
    void addPlayer(int teamId, String playerId) throws Exception;
    
    /**
     * Retire un joueur d'une équipe.
     */
    void removePlayer(int teamId, String playerId) throws Exception;
    
    /**
     * Récupère tous les joueurs d'une équipe (IDs seulement).
     */
    List<String> getTeamPlayers(int teamId) throws Exception;
    
    /**
     * Récupère tous les joueurs d'une équipe avec leurs informations complètes.
     */
    List<User> getTeamPlayersWithDetails(int teamId) throws Exception;
}
