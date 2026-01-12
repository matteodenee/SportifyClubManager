package com.sportify.manager.controllers;

import com.sportify.manager.facade.TrainingFacade;
import com.sportify.manager.frame.CoachDashboardFrame;
import com.sportify.manager.services.Training;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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
        int teamId = coachDashboard.getTrainingTeamId();

        if (date == null || time == null || clubId == null || teamId <= 0) {
            coachDashboard.showTrainingError("Veuillez renseigner une date, une heure et une équipe valides.");
            return;
        }

        boolean created = trainingFacade.createTraining(date, time, location, activity, clubId, teamId);
        if (created) {
            coachDashboard.showTrainingSuccess("Entrainement planifié avec succès.");
            coachDashboard.clearTrainingForm();
            coachDashboard.resetTrainingFilter();
            onRefresh();
        } else {
            coachDashboard.showTrainingError("Création impossible (vérifiez les champs et la date).");
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
        int teamId = coachDashboard.getTrainingFilterTeamId();
        List<Training> trainings = teamId > 0
                ? trainingFacade.listUpcomingByTeam(teamId, fromDate)
                : trainingFacade.listUpcoming(clubId, fromDate);
        if (teamId > 0 && (trainings == null || trainings.isEmpty())) {
            trainings = trainingFacade.listUpcoming(clubId, fromDate);
        }
        coachDashboard.setTrainings(trainings);
    }

    public void onUpdate() {
        if (coachDashboard == null) {
            return;
        }
        int trainingId = coachDashboard.getSelectedTrainingId();
        if (trainingId <= 0) {
            coachDashboard.showTrainingError("Sélectionnez un entrainement a modifier.");
            return;
        }
        LocalDate date = coachDashboard.getTrainingDate();
        LocalTime time = parseTime(coachDashboard.getTrainingTime());
        String location = coachDashboard.getTrainingLocation();
        String activity = coachDashboard.getTrainingActivity();
        Integer clubId = parseClubId(coachDashboard.getClubId());
        int teamId = coachDashboard.getTrainingTeamId();
        if (date == null || time == null || clubId == null || teamId <= 0) {
            coachDashboard.showTrainingError("Veuillez renseigner une date, une heure et une equipe valides.");
            return;
        }
        boolean updated = trainingFacade.updateTraining(trainingId, date, time, location, activity, clubId, teamId);
        if (updated) {
            coachDashboard.showTrainingSuccess("Entrainement modifie avec succes.");
            coachDashboard.clearTrainingForm();
            coachDashboard.resetTrainingFilter();
            onRefresh();
        } else {
            coachDashboard.showTrainingError("Modification impossible.");
        }
    }

    public void onDelete() {
        if (coachDashboard == null) {
            return;
        }
        int trainingId = coachDashboard.getSelectedTrainingId();
        if (trainingId <= 0) {
            coachDashboard.showTrainingError("Selectionnez un entrainement a supprimer.");
            return;
        }
        boolean deleted = trainingFacade.deleteTraining(trainingId);
        if (deleted) {
            coachDashboard.showTrainingSuccess("Entrainement supprime.");
            coachDashboard.clearTrainingForm();
            coachDashboard.resetTrainingFilter();
            onRefresh();
        } else {
            coachDashboard.showTrainingError("Suppression impossible.");
        }
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
