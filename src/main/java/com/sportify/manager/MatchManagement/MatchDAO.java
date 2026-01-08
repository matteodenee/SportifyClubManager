package com.sportify.manager.MatchManagement;


import com.sportify.manager.match.model.Match;

import java.time.LocalDateTime;
import java.util.List;

public abstract class MatchDAO {

    public abstract Match create(Match match) throws Exception;
    public abstract void update(Match match) throws Exception;

    public abstract Match getById(int id) throws Exception;
    public abstract List<Match> getAll() throws Exception;

    // utiles pour Composition UC
    public abstract int getTypeSportId(int matchId) throws Exception;
    public abstract LocalDateTime getCompositionDeadline(int matchId) throws Exception;
}