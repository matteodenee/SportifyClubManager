package com.sportify.manager.dao;

import com.sportify.manager.services.SmallEvent;
import java.util.List;

public interface SmallEventDAO {
    boolean create(SmallEvent event);
    List<SmallEvent> findByMatch(int matchId);
    List<SmallEvent> findByTeamAndPeriod(int teamId, String period);
    boolean deleteByMatch(int matchId);
}
