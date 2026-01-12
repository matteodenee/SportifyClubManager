package com.sportify.manager.controllers;

import com.sportify.manager.facade.EventFacade;
import com.sportify.manager.services.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class EventController {
    private static EventController instance = null;
    private final EventFacade eventFacade;
    private String lastError = "";

    private EventController() {
        this.eventFacade = EventFacade.getInstance();
    }

    public static synchronized EventController getInstance() {
        if (instance == null) {
            instance = new EventController();
        }
        return instance;
    }


    public String getLastError() {
        return lastError;
    }


    public boolean createEvent(String nom, String description, LocalDateTime dateDebut,
                              int dureeMinutes, String lieu, String type,
                              int clubId, String createurId) {
        try {
            if (nom == null || nom.trim().isEmpty()) {
                lastError = "Le nom de l'événement ne peut pas être vide";
                return false;
            }
            if (dateDebut == null) {
                lastError = "La date de début ne peut pas être null";
                return false;
            }
            if (dureeMinutes <= 0) {
                lastError = "La durée doit être positive";
                return false;
            }

            boolean result = eventFacade.createEvent(nom, description, dateDebut,
                                                    dureeMinutes, lieu, type, clubId, createurId);
            if (!result) {
                lastError = eventFacade.getLastError();
            } else {
                lastError = "";
            }
            return result;
        } catch (Exception e) {
            lastError = "Erreur lors de la création de l'événement: " + e.getMessage();
            return false;
        }
    }


    public Event getEventById(int eventId) {
        try {
            Event event = eventFacade.getEventById(eventId);
            if (event == null) {
                lastError = "Événement non trouvé avec l'ID: " + eventId;
            } else {
                lastError = "";
            }
            return event;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération de l'événement: " + e.getMessage();
            return null;
        }
    }

    /**
     * Récupère tous les événements d'un club.
     */
    public List<Event> getEventsByClub(int clubId) {
        try {
            List<Event> events = eventFacade.getEventsByClub(clubId);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des événements du club: " + e.getMessage();
            return null;
        }
    }


    public List<Event> getEventsByCreator(String createurId) {
        try {
            List<Event> events = eventFacade.getEventsByCreator(createurId);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des événements du créateur: " + e.getMessage();
            return null;
        }
    }


    public List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        try {
            if (start == null || end == null) {
                lastError = "Les dates de début et de fin ne peuvent pas être null";
                return null;
            }
            if (start.isAfter(end)) {
                lastError = "La date de début doit être avant la date de fin";
                return null;
            }

            List<Event> events = eventFacade.getEventsByDateRange(start, end);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des événements par plage de dates: " + e.getMessage();
            return null;
        }
    }


    public boolean updateEvent(int eventId, String nom, String description,
                              LocalDateTime dateDebut, int dureeMinutes,
                              String lieu, String type) {
        try {
            if (nom == null || nom.trim().isEmpty()) {
                lastError = "Le nom de l'événement ne peut pas être vide";
                return false;
            }
            if (dateDebut == null) {
                lastError = "La date de début ne peut pas être null";
                return false;
            }
            if (dureeMinutes <= 0) {
                lastError = "La durée doit être positive";
                return false;
            }

            boolean result = eventFacade.updateEvent(eventId, nom, description, dateDebut,
                                                    dureeMinutes, lieu, type);
            if (!result) {
                lastError = eventFacade.getLastError();
            } else {
                lastError = "";
            }
            return result;
        } catch (Exception e) {
            lastError = "Erreur lors de la mise à jour de l'événement: " + e.getMessage();
            return false;
        }
    }


    public boolean deleteEvent(int eventId) {
        try {
            boolean result = eventFacade.deleteEvent(eventId);
            if (!result) {
                lastError = eventFacade.getLastError();
            } else {
                lastError = "";
            }
            return result;
        } catch (Exception e) {
            lastError = "Erreur lors de la suppression de l'événement: " + e.getMessage();
            return false;
        }
    }


    public boolean rsvpToEvent(int eventId, String userId, String status) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                lastError = "L'ID utilisateur ne peut pas être vide";
                return false;
            }
            if (status == null || status.trim().isEmpty()) {
                lastError = "Le statut ne peut pas être vide";
                return false;
            }
            if (!status.equals("GOING") && !status.equals("NOT_GOING") && !status.equals("MAYBE")) {
                lastError = "Statut invalide. Doit être: GOING, NOT_GOING, ou MAYBE";
                return false;
            }

            boolean result = eventFacade.rsvpToEvent(eventId, userId, status);
            if (!result) {
                lastError = eventFacade.getLastError();
            } else {
                lastError = "";
            }
            return result;
        } catch (Exception e) {
            lastError = "Erreur lors de la gestion du RSVP: " + e.getMessage();
            return false;
        }
    }


    public Map<String, String> getEventParticipants(int eventId) {
        try {
            Map<String, String> participants = eventFacade.getEventParticipants(eventId);
            lastError = "";
            return participants;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des participants: " + e.getMessage();
            return null;
        }
    }


    public List<Event> getEventsByParticipant(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                lastError = "L'ID utilisateur ne peut pas être vide";
                return null;
            }

            List<Event> events = eventFacade.getEventsByParticipant(userId);
            lastError = "";
            return events;
        } catch (Exception e) {
            lastError = "Erreur lors de la récupération des événements du participant: " + e.getMessage();
            return null;
        }
    }
}
