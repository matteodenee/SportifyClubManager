package com.sportify.manager.facade;

import com.sportify.manager.services.ClubManager;

public class ClubFacade {
    private ClubManager clubManager;

    public ClubFacade() {
        clubManager = new ClubManager();  // Initialiser ClubManager
    }

    public void createClub(String name, String description, String type, String meetingSchedule, int maxCapacity) {
        clubManager.createClub(name, description, type, meetingSchedule, maxCapacity);
    }

    // Vous pouvez ajouter d'autres méthodes pour modifier et supprimer des clubs
}
