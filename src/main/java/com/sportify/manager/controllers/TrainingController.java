package com.sportify.manager.controllers;

import com.sportify.manager.facade.TrainingFacade;
import com.sportify.manager.frame.TrainingFrame;
import com.sportify.manager.services.Entrainement;
import com.sportify.manager.services.ParticipationStatus;
import com.sportify.manager.services.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class TrainingController {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    private TrainingFrame trainingFrame;
    private final TrainingFacade trainingFacade = TrainingFacade.getInstance();

    public void setTrainingFrame(TrainingFrame trainingFrame) {
        this.trainingFrame = trainingFrame;
    }

    public void onCreate() {
        if (trainingFrame == null) {
            return;
        }

        LocalDate date = trainingFrame.getTrainingDate();
        LocalTime time = parseTime(trainingFrame.getTrainingTime());
        String location = trainingFrame.getTrainingLocation();
        String activity = trainingFrame.getTrainingActivity();
        Integer clubId = parseClubId(trainingFrame.getClubId());

        if (date == null || time == null || clubId == null) {
            trainingFrame.showError("Veuillez renseigner une date, une heure et un club ID valides.");
            return;
        }

        boolean created = trainingFacade.createTraining(date, time, location, activity, clubId);
        if (created) {
            trainingFrame.showSuccess("Entrainement planifie avec succes.");
            trainingFrame.clearTrainingForm();
            onRefresh();
        } else {
            trainingFrame.showError("Creation impossible (verifie les champs et la date).");
        }
    }

    public void onRefresh() {
        if (trainingFrame == null) {
            return;
        }
        Integer clubId = parseClubId(trainingFrame.getClubId());
        if (clubId == null) {
            trainingFrame.showError("Club ID invalide pour le listing.");
            return;
        }
        LocalDate fromDate = trainingFrame.getFromDate();
        List<Entrainement> trainings = trainingFacade.listUpcoming(clubId, fromDate);
        trainingFrame.setTrainings(trainings);
        onLoadParticipation();
    }

    public void onMarkParticipation() {
        if (trainingFrame == null) {
            return;
        }
        int entrainementId = trainingFrame.getSelectedTrainingId();
        if (entrainementId <= 0) {
            trainingFrame.showError("Selectionne un entrainement.");
            return;
        }
        String userId = trainingFrame.getParticipationUserId();
        if (userId == null || userId.isBlank()) {
            trainingFrame.showError("User ID requis pour la participation.");
            return;
        }
        ParticipationStatus status = trainingFrame.getSelectedStatus();
        boolean updated = trainingFacade.markParticipation(entrainementId, userId, status);
        if (updated) {
            trainingFrame.showSuccess("Participation mise a jour.");
            onLoadParticipation();
        } else {
            trainingFrame.showError("Impossible de mettre a jour la participation.");
        }
    }

    public void onLoadParticipation() {
        if (trainingFrame == null) {
            return;
        }
        int entrainementId = trainingFrame.getSelectedTrainingId();
        if (entrainementId <= 0) {
            trainingFrame.clearParticipation();
            return;
        }
        Map<User, ParticipationStatus> participation = trainingFacade.getParticipation(entrainementId);
        trainingFrame.setParticipation(participation);
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
