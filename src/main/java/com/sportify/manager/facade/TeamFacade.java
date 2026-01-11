package com.sportify.manager.facade;

import com.sportify.manager.services.Team;
import com.sportify.manager.services.TeamManager;
import com.sportify.manager.services.User;

import java.util.List;

public class TeamFacade {
    private static TeamFacade instance = null;
    private final TeamManager teamManager;

    private TeamFacade() {
        this.teamManager = TeamManager.getInstance();
    }

    public static synchronized TeamFacade getInstance() {
        if (instance == null) {
            instance = new TeamFacade();
        }
        return instance;
    }

    public void createTeam(String nom, String categorie, int clubId, Integer coachId, Integer typeSportId) throws Exception {
        teamManager.createTeam(nom, categorie, clubId, coachId, typeSportId);
    }

    public Team getTeamById(int teamId) throws Exception {
        return teamManager.getTeamById(teamId);
    }

    public List<Team> getTeamsByClub(int clubId) throws Exception {
        return teamManager.getTeamsByClub(clubId);
    }

    public List<Team> getTeamsByCoach(String coachId) throws Exception {
        return teamManager.getTeamsByCoach(coachId);
    }

    public void updateTeam(Team team) throws Exception {
        teamManager.updateTeam(team);
    }

    public void deleteTeam(int teamId) throws Exception {
        teamManager.deleteTeam(teamId);
    }

    public void addPlayerToTeam(int teamId, String playerId) throws Exception {
        teamManager.addPlayerToTeam(teamId, playerId);
    }

    public void removePlayerFromTeam(int teamId, String playerId) throws Exception {
        teamManager.removePlayerFromTeam(teamId, playerId);
    }

    public List<User> getPlayersByTeam(int teamId) throws Exception {
        return teamManager.getPlayersByTeam(teamId);
    }
}
