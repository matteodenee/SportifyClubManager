package com.sportify.manager.facade;

import com.sportify.manager.services.Event;
import com.sportify.manager.services.EventManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Facade pour simplifier l'accès aux opérations sur les événements.
 * Pattern Singleton.
 */
public class EventFacade {
    private static EventFacade instance = null;
    private final EventManager eventManager;

    private EventFacade() {
        this.eventManager = EventManager.getInstance();
    }

    public static synchronized EventFacade getInstance() {
        if (instance == null) {
            instance = new EventFacade();
        }
        return instance;
    }

    /**
     * Crée un nouvel événement.
     */
    public boolean createEvent(String nom, String description, LocalDateTime dateDebut,
                              int dureeMinutes, String lieu, String type,
                              int clubId, String createurId) {
        return eventManager.createEvent(nom, description, dateDebut, dureeMinutes,
                                       lieu, type, clubId, createurId);
    }

    /**
     * Récupère un événement par son ID.
     */
    public Event getEventById(int eventId) {
        return eventManager.getEventById(eventId);
    }

    /**
     * Récupère tous les événements d'un club.
     */
    public List<Event> getEventsByClub(int clubId) {
        return eventManager.getEventsByClub(clubId);
    }

    /**
     * Récupère tous les événements créés par un utilisateur.
     */
    public List<Event> getEventsByCreator(String createurId) {
        return eventManager.getEventsByCreator(createurId);
    }

    /**
     * Récupère tous les événements entre deux dates.
     */
    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventManager.getEventsByDateRange(start, end);
    }

    /**
     * Met à jour un événement existant.
     */
    public boolean updateEvent(int eventId, String nom, String description,
                              LocalDateTime dateDebut, int dureeMinutes,
                              String lieu, String type) {
        return eventManager.updateEvent(eventId, nom, description, dateDebut,
                                       dureeMinutes, lieu, type);
    }

    /**
     * Supprime un événement.
     */
    public boolean deleteEvent(int eventId) {
        return eventManager.deleteEvent(eventId);
    }

    /**
     * Gère la participation d'un utilisateur à un événement (RSVP).
     */
    public boolean rsvpToEvent(int eventId, String userId, String status) {
        return eventManager.rsvpToEvent(eventId, userId, status);
    }

    /**
     * Récupère tous les participants d'un événement.
     */
    public Map<String, String> getEventParticipants(int eventId) {
        return eventManager.getEventParticipants(eventId);
    }

    /**
     * Récupère tous les événements auxquels un utilisateur participe.
     */
    public List<Event> getEventsByParticipant(String userId) {
        return eventManager.getEventsByParticipant(userId);
    }

    /**
     * Récupère la dernière erreur du manager.
     */
    public String getLastError() {
        return eventManager.getLastError();
    }
}
