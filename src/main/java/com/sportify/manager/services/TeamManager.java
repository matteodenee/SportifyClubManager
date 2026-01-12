package com.sportify.manager.services;

import com.sportify.manager.dao.PostgresUserDAO;
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

    public void createTeam(String nom, String categorie, int clubId, String coachId, Integer typeSportId) throws Exception {
        if (nom == null || nom.isEmpty()) {
            throw new Exception("Le nom de l'équipe est obligatoire.");
        }
        if (coachId != null && !coachId.isBlank()) {
            if (typeSportId == null || typeSportId <= 0) {
                throw new Exception("Type sport de l'équipe non défini.");
            }
            boolean hasLicence = PostgresUserDAO.getInstance().hasActiveLicenceForSport(coachId.trim(), typeSportId);
            if (!hasLicence) {
                throw new Exception("Le coach n'a pas de licence ACTIVE pour ce sport.");
            }
        }

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
        if (team.getCoachId() != null && !team.getCoachId().isBlank()) {
            Integer teamSportId = team.getTypeSportId();
            if (teamSportId == null || teamSportId <= 0) {
                throw new Exception("Type sport de l'équipe non défini.");
            }
            boolean hasLicence = PostgresUserDAO.getInstance().hasActiveLicenceForSport(team.getCoachId().trim(), teamSportId);
            if (!hasLicence) {
                throw new Exception("Le coach n'a pas de licence ACTIVE pour ce sport.");
            }
        }
        teamDAO.update(team);
    }

    public void deleteTeam(int teamId) throws Exception {
        teamDAO.delete(teamId);
    }

    public void addPlayerToTeam(int teamId, String userId) throws Exception {
        if (teamId <= 0) {
            throw new Exception("ID d'équipe invalide.");
        }
        if (userId == null || userId.isBlank()) {
            throw new Exception("ID joueur invalide.");
        }
        Team team = teamDAO.findById(teamId);
        if (team == null) {
            throw new Exception("Équipe introuvable.");
        }
        int memberClubId = PostgresUserDAO.getInstance().getClubIdByMember(userId.trim());
        if (memberClubId <= 0) {
            throw new Exception("Ce joueur n'est pas membre d'un club.");
        }
        if (memberClubId != team.getClubId()) {
            throw new Exception("Le joueur n'appartient pas au même club que l'équipe.");
        }
        Integer teamSportId = team.getTypeSportId();
        if (teamSportId == null || teamSportId <= 0) {
            throw new Exception("Type sport de l'équipe non défini.");
        }
        boolean hasLicence = PostgresUserDAO.getInstance().hasActiveLicenceForSport(userId.trim(), teamSportId);
        if (!hasLicence) {
            throw new Exception("Le joueur n'a pas de licence ACTIVE pour ce sport.");
        }
        teamDAO.addPlayerToTeam(teamId, userId);
    }

    public void removePlayerFromTeam(int teamId, String userId) throws Exception {
        teamDAO.removePlayerFromTeam(teamId, userId);
    }

    public List<User> getPlayersByTeam(int teamId) throws Exception {
        return teamDAO.findPlayersByTeamId(teamId);
    }

    public List<Team> getTeamsByMember(String userId) throws Exception {
        return teamDAO.findAllByMemberId(userId);
    }
}
