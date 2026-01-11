package com.sportify.manager.controllers;

import com.sportify.manager.facade.TeamFacade;
import com.sportify.manager.services.Team;

import java.util.List;

/**
 * Contrôleur pour la gestion des équipes.
 * Pattern Singleton avec gestion d'erreurs via lastError.
 */
public class TeamController {
    private static TeamController instance = null;
    private final TeamFacade teamFacade;
    private String lastError = "";

    private TeamController() {
        this.teamFacade = TeamFacade.getInstance();
    }

    public static synchronized TeamController getInstance() {
        if (instance == null) {
            instance = new TeamController();
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
    public boolean createTeam(String nom, String categorie, int clubId, String coachId, Integer typeSportId) {
        try {
            if (nom == null || nom.trim().isEmpty()) {
                lastError = "Le nom de l'équipe ne peut pas être vide";
                return false;
            }

            teamFacade.createTeam(nom, categorie, clubId, null, typeSportId);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de la création de l'équipe: " + e.getMessage();
            return false;
        }
    }

    /**
     * Récupère une équipe par son ID.
     */
    public Team getTeamById(int teamId) {
        try {
            Team team = teamFacade.getTeamById(teamId);
            if (team == null) {
                lastError = "Équipe non trouvée avec l'ID: " + teamId;
            } else {
                lastError = "";
            }
            return team;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération de l'équipe: " + e.getMessage();
            return null;
        }
    }

    /**
     * Récupère toutes les équipes d'un club.
     */
    public List<Team> getTeamsByClub(int clubId) {
        try {
            List<Team> teams = teamFacade.getTeamsByClub(clubId);
            lastError = "";
            return teams;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des équipes du club: " + e.getMessage();
            return null;
        }
    }

    /**
     * Met à jour une équipe.
     */
    public boolean updateTeam(Team team) {
        try {
            if (team == null) {
                lastError = "L'équipe ne peut pas être null";
                return false;
            }
            if (team.getNom() == null || team.getNom().trim().isEmpty()) {
                lastError = "Le nom de l'équipe ne peut pas être vide";
                return false;
            }

            teamFacade.updateTeam(team);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de la mise à jour de l'équipe: " + e.getMessage();
            return false;
        }
    }

    /**
     * Supprime une équipe.
     */
    public boolean deleteTeam(int teamId) {
        try {
            teamFacade.deleteTeam(teamId);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de la suppression de l'équipe: " + e.getMessage();
            return false;
        }
    }

    /**
     * Ajoute un joueur à une équipe.
     */
    public boolean addPlayerToTeam(int teamId, String playerId) {
        try {
            if (playerId == null || playerId.trim().isEmpty()) {
                lastError = "L'ID du joueur ne peut pas être vide";
                return false;
            }

            teamFacade.addPlayerToTeam(teamId, playerId);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de l'ajout du joueur: " + e.getMessage();
            return false;
        }
    }

    /**
     * Retire un joueur d'une équipe.
     */
    public boolean removePlayerFromTeam(int teamId, String playerId) {
        try {
            if (playerId == null || playerId.trim().isEmpty()) {
                lastError = "L'ID du joueur ne peut pas être vide";
                return false;
            }

            teamFacade.removePlayerFromTeam(teamId, playerId);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors du retrait du joueur: " + e.getMessage();
            return false;
        }
    }
}
