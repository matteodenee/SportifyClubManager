package com.sportify.manager.controllers;

import com.sportify.manager.facade.SmallEventFacade;
import com.sportify.manager.services.SmallEvent;
import java.util.List;

public class SmallEventController {
    private final SmallEventFacade facade = SmallEventFacade.getInstance();

    public boolean handleCreate(SmallEvent event) {
        return facade.createEvent(event);
    }

    public List<SmallEvent> handleGetByMatch(int matchId) {
        return facade.getEventsByMatch(matchId);
    }

    public List<SmallEvent> handleGetByTeam(int teamId, String period) {
        return facade.getEventsByTeam(teamId, period);
    }

    public boolean handleDeleteByMatch(int matchId) {
        return facade.deleteEventsByMatch(matchId);
    }
}
