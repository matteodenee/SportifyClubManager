package com.sportify.manager.services;

import com.sportify.manager.dao.EventDAO;
import com.sportify.manager.persistence.AbstractFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Manager pour la gestion des événements.
 * Pattern Singleton avec gestion d'erreurs.
 */
public class EventManager {
    private static EventManager instance;
    private final EventDAO eventDAO;
    private String lastError = "";

    private EventManager() {
        this.eventDAO = AbstractFactory.getFactory().createEventDAO();
    }

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * Récupère la dernière erreur.
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * Crée un nouvel événement.
     */
    public boolean createEvent(String nom, String description, LocalDateTime dateDebut,
                               int dureeMinutes, String lieu, String type,
                               int clubId, String createurId) {
        try {
            if (nom == null || nom.trim().isEmpty()) {
                lastError = "Le nom de l'événement est requis";
                return false;
            }
            if (dateDebut == null) {
                lastError = "La date de début est requise";
                return false;
            }
            if (dateDebut.isBefore(LocalDateTime.now())) {
                lastError = "L'événement ne peut pas être dans le passé";
                return false;
            }
            if (dureeMinutes <= 0) {
                lastError = "La durée doit être positive";
                return false;
            }

            Event event = new Event(nom, description, dateDebut, dureeMinutes, lieu, type, clubId, createurId);
            eventDAO.create(event);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de la création: " + e.getMessage();
            return false;
        }
    }

    /**
     * Récupère un événement par son ID.
     */
    public Event getEventById(int eventId) {
        try {
            Event event = eventDAO.findById(eventId);
            lastError = "";
            return event;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération: " + e.getMessage();
            return null;
        }
    }

    /**
     * Récupère tous les événements d'un club.
     */
    public List<Event> getEventsByClub(int clubId) {
        try {
            List<Event> events = eventDAO.findAllByClubId(clubId);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération: " + e.getMessage();
            return null;
        }
    }

    /**
     * Récupère tous les événements créés par un utilisateur.
     */
    public List<Event> getEventsByCreator(String createurId) {
        try {
            List<Event> events = eventDAO.findAllByCreatorId(createurId);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération: " + e.getMessage();
            return null;
        }
    }

    /**
     * Récupère tous les événements entre deux dates.
     */
    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        try {
            List<Event> events = eventDAO.findByDateRange(start, end);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération: " + e.getMessage();
            return null;
        }
    }

    /**
     * Met à jour un événement.
     */
    public boolean updateEvent(int eventId, String nom, String description,
                               LocalDateTime dateDebut, int dureeMinutes,
                               String lieu, String type) {
        try {
            Event event = eventDAO.findById(eventId);
            if (event == null) {
                lastError = "Événement non trouvé";
                return false;
            }

            event.setNom(nom);
            event.setDescription(description);
            event.setDateDebut(dateDebut);
            event.setDureeMinutes(dureeMinutes);
            event.setLieu(lieu);
            event.setType(type);

            eventDAO.update(event);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de la mise à jour: " + e.getMessage();
            return false;
        }
    }

    /**
     * Supprime un événement.
     */
    public boolean deleteEvent(int eventId) {
        try {
            eventDAO.delete(eventId);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors de la suppression: " + e.getMessage();
            return false;
        }
    }

    /**
     * Gère la participation d'un utilisateur à un événement (RSVP).
     */
    public boolean rsvpToEvent(int eventId, String userId, String status) {
        try {
            if (!status.equals("GOING") && !status.equals("NOT_GOING") && !status.equals("MAYBE")) {
                lastError = "Statut invalide. Doit être: GOING, NOT_GOING, ou MAYBE";
                return false;
            }

            eventDAO.setParticipantStatus(eventId, userId, status);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "Erreur lors du RSVP: " + e.getMessage();
            return false;
        }
    }

    /**
     * Récupère tous les participants d'un événement.
     */
    public Map<String, String> getEventParticipants(int eventId) {
        try {
            Map<String, String> participants = eventDAO.getParticipants(eventId);
            lastError = "";
            return participants;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des participants: " + e.getMessage();
            return null;
        }
    }

    /**
     * Récupère tous les événements auxquels un utilisateur participe.
     */
    public List<Event> getEventsByParticipant(String userId) {
        try {
            List<Event> events = eventDAO.findByParticipant(userId);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération: " + e.getMessage();
            return null;
        }
    }
}
