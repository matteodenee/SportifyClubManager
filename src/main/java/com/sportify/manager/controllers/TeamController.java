package com.sportify.manager.controllers;

import com.sportify.manager.facade.TeamFacade;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;
import java.util.List;

public class TeamController {
    private static TeamController instance;
    private final TeamFacade teamFacade;
    private String lastError;

    private TeamController() {
        this.teamFacade = TeamFacade.getInstance();
    }

    public static synchronized TeamController getInstance() {
        if (instance == null) {
            instance = new TeamController();
        }
        return instance;
    }

    public boolean handleCreateTeam(String nom, String categorie, int clubId, String coachId, Integer typeSportId) {
        try {
            if (clubId <= 0) {
                lastError = "ID Club invalide";
                return false;
            }

            teamFacade.createTeam(nom, categorie, clubId, coachId, typeSportId);
            lastError = null;
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public List<Team> handleGetTeams(int clubId) {
        try {
            lastError = null;
            return teamFacade.getTeamsByClub(clubId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public Team handleGetTeamById(int teamId) {
        try {
            lastError = null;
            return teamFacade.getTeamById(teamId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public boolean handleUpdateTeam(Team team) {
        try {
            teamFacade.updateTeam(team);
            lastError = null;
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean handleDeleteTeam(int teamId) {
        try {
            teamFacade.deleteTeam(teamId);
            lastError = null;
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean handleAddPlayer(int teamId, String userId) {
        try {
            teamFacade.addPlayerToTeam(teamId, userId);
            lastError = null;
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean handleRemovePlayer(int teamId, String userId) {
        try {
            teamFacade.removePlayerFromTeam(teamId, userId);
            lastError = null;
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public List<User> handleGetPlayers(int teamId) {
        try {
            lastError = null;
            return teamFacade.getPlayersByTeam(teamId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public List<Team> handleGetTeamsByMember(String userId) {
        try {
            lastError = null;
            return teamFacade.getTeamsByMember(userId);
        } catch (Exception e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    public String getLastError() {
        return lastError;
    }
}
