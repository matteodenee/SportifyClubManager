package com.sportify.manager.facade;

import com.sportify.manager.services.ClubManager;

public class ClubFacade {
    private ClubManager clubManager;

    public ClubFacade() {
        clubManager = new ClubManager();
    }

    public void createClub(String name, String description, int sportId, String type, String meetingSchedule, int maxCapacity) {
        // Transmission au manager
        clubManager.createClub(name, description, sportId, type, meetingSchedule, maxCapacity);
    }
}