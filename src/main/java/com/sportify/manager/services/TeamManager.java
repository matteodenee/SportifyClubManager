package com.sportify.manager.services;

import com.sportify.manager.dao.TeamDAO;
import com.sportify.manager.persistence.AbstractFactory;

import java.util.List;

/**
 * Manager pour la gestion des équipes.
 * Pattern Singleton.
 */
public class TeamManager {
    private static TeamManager instance;
    private final TeamDAO teamDAO;
    private String lastError = "";

    private TeamManager() {
        this.teamDAO = AbstractFactory.getFactory().createTeamDAO();
    }

    public static synchronized TeamManager getInstance() {
        if (instance == null) {
            instance = new TeamManager();
        }
        return instance;
    }

    /**
     * Récupère la dernière erreur.
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * Crée une nouvelle équipe.
     */
    public void createTeam(String nom, String categorie, int clubId, Integer coachId, Integer typeSportId) throws Exception {
        if (nom == null || nom.trim().isEmpty()) {
            throw new Exception("Le nom de l'équipe est requis");
        }

        // Convertir Integer coachId en String (peut être null)
        String coachIdStr = (coachId != null) ? String.valueOf(coachId) : null;
        
        Team team = new Team(nom, categorie, clubId, coachIdStr, typeSportId);
        teamDAO.create(team);
    }

    /**
     * Récupère une équipe par son ID.
     */
    public Team getTeamById(int teamId) throws Exception {
        return teamDAO.findById(teamId);
    }

    /**
     * Récupère toutes les équipes d'un club.
     */
    public List<Team> getTeamsByClub(int clubId) throws Exception {
        return teamDAO.findAllByClubId(clubId);
    }

    /**
     * Récupère toutes les équipes d'un coach.
     */
    public List<Team> getTeamsByCoach(String coachId) throws Exception {
        return teamDAO.findAllByCoachId(coachId);
    }

    /**
     * Met à jour une équipe.
     */
    public void updateTeam(Team team) throws Exception {
        if (team == null) {
            throw new Exception("L'équipe ne peut pas être null");
        }
        teamDAO.update(team);
    }

    /**
     * Supprime une équipe.
     */
    public void deleteTeam(int teamId) throws Exception {
        teamDAO.delete(teamId);
    }

    /**
     * Ajoute un joueur à une équipe.
     */
    public void addPlayerToTeam(int teamId, String playerId) throws Exception {
        teamDAO.addPlayer(teamId, playerId);
    }

    /**
     * Retire un joueur d'une équipe.
     */
    public void removePlayerFromTeam(int teamId, String playerId) throws Exception {
        teamDAO.removePlayer(teamId, playerId);
    }

    /**
     * Récupère tous les joueurs d'une équipe avec leurs informations complètes.
     */
    public List<User> getPlayersByTeam(int teamId) throws Exception {
        return teamDAO.getTeamPlayersWithDetails(teamId);
    }

    /**
     * Récupère tous les IDs des joueurs d'une équipe.
     */
    public List<String> getTeamPlayers(int teamId) throws Exception {
        return teamDAO.getTeamPlayers(teamId);
    }
}
