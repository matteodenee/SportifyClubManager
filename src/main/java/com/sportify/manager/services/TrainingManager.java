package com.sportify.manager.services;

import com.sportify.manager.dao.TrainingDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TrainingManager {

    private static TrainingManager instance;

    private TrainingManager() {
    }

    public static synchronized TrainingManager getTrainingManager() {
        if (instance == null) {
            instance = new TrainingManager();
        }
        return instance;
    }

    private TrainingDAO getDAO() {
        return AbstractFactory.getFactory().createTrainingDAO();
    }

    public boolean createTraining(LocalDate date, LocalTime time, String location, String activity, int clubId) {
        if (!isValid(date, time, location, activity, clubId)) {
            return false;
        }
        Training entrainement = new Training(date, time, location, activity, clubId);
        return getDAO().create(entrainement);
    }

    public boolean createTraining(LocalDate date, LocalTime time, String location, String activity, int clubId, int teamId) {
        if (!isValid(date, time, location, activity, clubId)) {
            return false;
        }
        if (teamId <= 0) {
            return false;
        }
        Training entrainement = new Training(date, time, location, activity, clubId, teamId);
        return getDAO().create(entrainement);
    }

    public boolean updateTraining(int id, LocalDate date, LocalTime time, String location, String activity, int clubId, int teamId) {
        if (id <= 0) {
            return false;
        }
        if (!isValid(date, time, location, activity, clubId)) {
            return false;
        }
        if (teamId <= 0) {
            return false;
        }
        Training entrainement = new Training(id, date, time, location, activity, clubId, teamId);
        return getDAO().update(entrainement);
    }

    public boolean deleteTraining(int id) {
        if (id <= 0) {
            return false;
        }
        return getDAO().delete(id);
    }

    public List<Training> listUpcomingTrainings(int clubId, LocalDate fromDate) {
        if (clubId <= 0) {
            return Collections.emptyList();
        }
        return getDAO().getUpcomingByClub(clubId, fromDate);
    }

    public List<Training> listUpcomingTrainingsByTeam(int teamId, LocalDate fromDate) {
        if (teamId <= 0) {
            return Collections.emptyList();
        }
        return getDAO().getUpcomingByTeam(teamId, fromDate);
    }

    public boolean markParticipation(int entrainementId, String userId, ParticipationStatus status) {
        if (entrainementId <= 0 || userId == null || userId.isBlank()) {
            return false;
        }
        ParticipationStatus effectiveStatus = (status == null) ? ParticipationStatus.PENDING : status;
        return getDAO().setParticipation(entrainementId, userId, effectiveStatus);
    }

    public Map<User, ParticipationStatus> getParticipation(int entrainementId) {
        if (entrainementId <= 0) {
            return Collections.emptyMap();
        }
        return getDAO().getParticipation(entrainementId);
    }

    private boolean isValid(LocalDate date, LocalTime time, String location, String activity, int clubId) {
        if (date == null || time == null) {
            return false;
        }
        if (location == null || location.isBlank()) {
            return false;
        }
        if (activity == null || activity.isBlank()) {
            return false;
        }
        if (clubId <= 0) {
            return false;
        }
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        return dateTime.isAfter(LocalDateTime.now());
    }
}
