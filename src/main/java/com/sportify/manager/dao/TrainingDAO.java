package com.sportify.manager.dao;

import com.sportify.manager.services.Training;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrainingDAO {

    boolean create(Training entrainement);

    List<Training> getUpcomingByClub(int clubId, LocalDate fromDate);
    List<Training> getUpcomingByTeam(int teamId, LocalDate fromDate);

    Optional<Training> getById(int id);

    boolean setParticipation(int entrainementId, String userId, ParticipationStatus status);

    Map<User, ParticipationStatus> getParticipation(int entrainementId);
}
