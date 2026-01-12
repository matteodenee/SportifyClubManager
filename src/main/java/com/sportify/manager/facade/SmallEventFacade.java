package com.sportify.manager.facade;

import com.sportify.manager.services.SmallEvent;
import com.sportify.manager.services.SmallEventManager;
import java.util.List;

public class SmallEventFacade {
    private static SmallEventFacade instance;
    private final SmallEventManager manager;

    private SmallEventFacade() {
        this.manager = SmallEventManager.getInstance();
    }

    public static synchronized SmallEventFacade getInstance() {
        if (instance == null) {
            instance = new SmallEventFacade();
        }
        return instance;
    }

    public boolean createEvent(SmallEvent event) {
        return manager.createEvent(event);
    }

    public List<SmallEvent> getEventsByMatch(int matchId) {
        return manager.getEventsByMatch(matchId);
    }

    public List<SmallEvent> getEventsByTeam(int teamId, String period) {
        return manager.getEventsByTeam(teamId, period);
    }

    public boolean deleteEventsByMatch(int matchId) {
        return manager.deleteEventsByMatch(matchId);
    }
}
