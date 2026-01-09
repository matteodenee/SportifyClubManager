package com.sportify.manager.dao;

import com.sportify.manager.services.Match; // On utilise le Match que tu as dans services
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface adaptée pour l'intégration globale du projet.
 * Elle permet de lier MatchManagement avec StatManage.
 */
public interface MatchDAO {

    Match create(Match match) throws Exception;

    void update(Match match) throws Exception;

    Match getById(int id) throws Exception;

    List<Match> getAll() throws Exception;

    List<Match> getByClub(int clubId) throws Exception;

    // Utiles pour le Use Case Composition
    int getTypeSportId(int matchId) throws Exception;

    LocalDateTime getCompositionDeadline(int matchId) throws Exception;
}
