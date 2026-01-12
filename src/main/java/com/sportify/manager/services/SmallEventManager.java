package com.sportify.manager.services;

import com.sportify.manager.dao.SmallEventDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.util.Collections;
import java.util.List;

public class SmallEventManager {
    private static SmallEventManager instance;
    private final SmallEventDAO smallEventDAO;

    private SmallEventManager() {
        this.smallEventDAO = AbstractFactory.getFactory().createSmallEventDAO();
    }

    public static synchronized SmallEventManager getInstance() {
        if (instance == null) {
            instance = new SmallEventManager();
        }
        return instance;
    }

    public boolean createEvent(SmallEvent event) {
        if (event == null || event.getTeamId() <= 0 || event.getType() == null || event.getType().isBlank()) {
            return false;
        }
        return smallEventDAO.create(event);
    }

    public List<SmallEvent> getEventsByMatch(int matchId) {
        if (matchId <= 0) {
            return Collections.emptyList();
        }
        return smallEventDAO.findByMatch(matchId);
    }

    public List<SmallEvent> getEventsByTeam(int teamId, String period) {
        if (teamId <= 0 || period == null || period.isBlank()) {
            return Collections.emptyList();
        }
        return smallEventDAO.findByTeamAndPeriod(teamId, period);
    }

    public boolean deleteEventsByMatch(int matchId) {
        if (matchId <= 0) {
            return false;
        }
        return smallEventDAO.deleteByMatch(matchId);
    }
}
