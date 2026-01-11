package com.sportify.manager.controllers;

import com.sportify.manager.facade.TrainingFacade;
import com.sportify.manager.frame.CoachDashboardFrame;
import com.sportify.manager.services.Training;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import com.sportify.manager.dao.TrainingDAO;

public class TrainingController {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    private CoachDashboardFrame coachDashboard;
    private final TrainingFacade trainingFacade = TrainingFacade.getInstance();

    public void setCoachDashboard(CoachDashboardFrame coachDashboard) {
        this.coachDashboard = coachDashboard;
    }

    public void onCreate() {
        if (coachDashboard == null) {
            return;
        }

        LocalDate date = coachDashboard.getTrainingDate();
        LocalTime time = parseTime(coachDashboard.getTrainingTime());
        String location = coachDashboard.getTrainingLocation();
        String activity = coachDashboard.getTrainingActivity();
        Integer clubId = parseClubId(coachDashboard.getClubId());

        if (date == null || time == null || clubId == null) {
            coachDashboard.showTrainingError("Veuillez renseigner une date, une heure et un club ID valides.");
            return;
        }

        boolean created = trainingFacade.createTraining(date, time, location, activity, clubId);
        if (created) {
            coachDashboard.showTrainingSuccess("✅ Entrainement planifié avec succès.");
            coachDashboard.clearTrainingForm();
            onRefresh();
        } else {
            coachDashboard.showTrainingError("❌ Création impossible (vérifiez les champs et la date).");
        }
    }

    public void onRefresh() {
        if (coachDashboard == null) {
            return;
        }
        Integer clubId = parseClubId(coachDashboard.getClubId());
        if (clubId == null) {
            coachDashboard.showTrainingError("Club ID invalide pour le listing.");
            return;
        }
        LocalDate fromDate = coachDashboard.getFromDate();
        List<Training> trainings = trainingFacade.listUpcoming(clubId, fromDate);
        coachDashboard.setTrainings(trainings);
        onLoadParticipation();
    }

    public void onMarkParticipation() {
        if (coachDashboard == null) {
            return;
        }
        int entrainementId = coachDashboard.getSelectedTrainingId();
        if (entrainementId <= 0) {
            coachDashboard.showTrainingError("❌ Sélectionnez un entrainement.");
            return;
        }
        String userId = coachDashboard.getParticipationUserId();
        if (userId == null || userId.isBlank()) {
            coachDashboard.showTrainingError("❌ User ID requis pour la participation.");
            return;
        }
        ParticipationStatus status = coachDashboard.getSelectedStatus();
        boolean updated = trainingFacade.markParticipation(entrainementId, userId, status);
        if (updated) {
            coachDashboard.showTrainingSuccess("✅ Participation mise à jour.");
            onLoadParticipation();
        } else {
            coachDashboard.showTrainingError("❌ Impossible de mettre à jour la participation.");
        }
    }

    public void onLoadParticipation() {
        if (coachDashboard == null) {
            return;
        }
        int entrainementId = coachDashboard.getSelectedTrainingId();
        if (entrainementId <= 0) {
            coachDashboard.clearParticipation();
            return;
        }
        Map<User, ParticipationStatus> participation = trainingFacade.getParticipation(entrainementId);
        coachDashboard.setParticipation(participation);
    }

    private LocalTime parseTime(String timeText) {
        if (timeText == null || timeText.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(timeText.trim(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Integer parseClubId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }



}