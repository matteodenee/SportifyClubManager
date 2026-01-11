package com.sportify.manager.dao;

import com.sportify.manager.services.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventDAO {
    void create(Event event) throws Exception;
    Event findById(int id) throws Exception;
    List<Event> findAllByClubId(int clubId) throws Exception;
    List<Event> findAllByCreatorId(String creatorId) throws Exception;
    List<Event> findByDateRange(LocalDateTime start, LocalDateTime end) throws Exception;
    void update(Event event) throws Exception;
    void delete(int id) throws Exception;

    // Gestion des participations (RSVP)
    void setParticipantStatus(int eventId, String userId, String status) throws Exception;
    Map<String, String> getParticipants(int eventId) throws Exception;
    List<Event> findByParticipant(String userId) throws Exception;
}
