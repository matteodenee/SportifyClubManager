package com.sportify.manager.dao;

import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;
import java.util.List;

public interface TeamDAO {
    Team create(Team team) throws Exception;
    Team findById(int id) throws Exception;
    List<Team> findAllByClubId(int clubId) throws Exception;
    void update(Team team) throws Exception;
    void delete(int id) throws Exception;

    void addPlayerToTeam(int teamId, String userId) throws Exception;
    void removePlayerFromTeam(int teamId, String userId) throws Exception;
    List<User> findPlayersByTeamId(int teamId) throws Exception;
    List<Team> findAllByMemberId(String userId) throws Exception;
}
