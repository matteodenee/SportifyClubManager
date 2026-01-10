package com.sportify.manager.dao;

import com.sportify.manager.services.Entrainement;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrainingDAO {

    boolean create(Entrainement entrainement);

    List<Entrainement> getUpcomingByClub(int clubId, LocalDate fromDate);

    Optional<Entrainement> getById(int id);

    boolean setParticipation(int entrainementId, String userId, ParticipationStatus status);

    Map<User, ParticipationStatus> getParticipation(int entrainementId);
}
