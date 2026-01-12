package com.sportify.manager.facade;

import com.sportify.manager.services.Training;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.TrainingManager;
import com.sportify.manager.services.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class TrainingFacade {

    private static TrainingFacade instance;

    private TrainingFacade() {
    }

    public static synchronized TrainingFacade getInstance() {
        if (instance == null) {
            instance = new TrainingFacade();
        }
        return instance;
    }

    public boolean createTraining(LocalDate date, LocalTime time, String location, String activity, int clubId) {
        return TrainingManager.getTrainingManager().createTraining(date, time, location, activity, clubId);
    }

    public boolean createTraining(LocalDate date, LocalTime time, String location, String activity, int clubId, int teamId) {
        return TrainingManager.getTrainingManager().createTraining(date, time, location, activity, clubId, teamId);
    }

    public boolean updateTraining(int id, LocalDate date, LocalTime time, String location, String activity, int clubId, int teamId) {
        return TrainingManager.getTrainingManager().updateTraining(id, date, time, location, activity, clubId, teamId);
    }

    public boolean deleteTraining(int id) {
        return TrainingManager.getTrainingManager().deleteTraining(id);
    }

    public List<Training> listUpcoming(int clubId, LocalDate fromDate) {
        return TrainingManager.getTrainingManager().listUpcomingTrainings(clubId, fromDate);
    }

    public List<Training> listUpcomingByTeam(int teamId, LocalDate fromDate) {
        return TrainingManager.getTrainingManager().listUpcomingTrainingsByTeam(teamId, fromDate);
    }

    public boolean markParticipation(int entrainementId, String userId, ParticipationStatus status) {
        return TrainingManager.getTrainingManager().markParticipation(entrainementId, userId, status);
    }

    public Map<User, ParticipationStatus> getParticipation(int entrainementId) {
        return TrainingManager.getTrainingManager().getParticipation(entrainementId);
    }
}
