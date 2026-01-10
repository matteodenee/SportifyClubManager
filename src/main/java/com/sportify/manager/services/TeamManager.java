package com.sportify.manager.services;

import com.sportify.manager.dao.TeamDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.util.List;

public class TeamManager {
    private static TeamManager instance;
    private final TeamDAO teamDAO;

    private TeamManager() {
        this.teamDAO = AbstractFactory.getFactory().createTeamDAO();
    }

    public static synchronized TeamManager getInstance() {
        if (instance == null) {
            instance = new TeamManager();
        }
        return instance;
    }

    public void createTeam(String nom, String categorie, int clubId, Integer coachId, Integer typeSportId) throws Exception {
        if (nom == null || nom.isEmpty()) {
            throw new Exception("Le nom de l'équipe est obligatoire.");
        }
        // Ici vous pouvez ajouter des vérifications (ex: si le coach existe, si le sport existe via leurs DAOs respectifs)
        
        Team newTeam = new Team(nom, categorie, clubId, coachId, typeSportId);
        teamDAO.create(newTeam);
    }

    public List<Team> getTeamsByClub(int clubId) throws Exception {
        return teamDAO.findAllByClubId(clubId);
    }

    public Team getTeamById(int teamId) throws Exception {
        return teamDAO.findById(teamId);
    }

    public void updateTeam(Team team) throws Exception {
        if (team.getId() <= 0) {
            throw new Exception("ID d'équipe invalide.");
        }
        teamDAO.update(team);
    }

    public void deleteTeam(int teamId) throws Exception {
        teamDAO.delete(teamId);
    }

    public void addPlayerToTeam(int teamId, String userId) throws Exception {
        // Logique métier : Vérifier si le joueur a le droit d'être dans cette équipe (âge, licence...)
        teamDAO.addPlayerToTeam(teamId, userId);
    }
    
    public void removePlayerFromTeam(int teamId, String userId) throws Exception {
        teamDAO.removePlayerFromTeam(teamId, userId);
    }

    public List<User> getPlayersByTeam(int teamId) throws Exception {
        return teamDAO.findPlayersByTeamId(teamId);
    }
}
