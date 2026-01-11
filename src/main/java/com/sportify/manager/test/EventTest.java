package com.sportify.manager.test;

import com.sportify.manager.controllers.EventController;
import com.sportify.manager.services.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Classe de test pour la gestion des événements.
 * Test des opérations CRUD et de la gestion des participants (RSVP).
 */
public class EventTest {
    
    public static void main(String[] args) {
        System.out.println("=== TEST EVENT MANAGEMENT ===\n");
        
        EventController eventController = EventController.getInstance();
        
        // Test 1: Création d'un événement
        System.out.println("--- Test 1: Création d'événement ---");
        LocalDateTime eventDate = LocalDateTime.of(2026, 2, 15, 18, 30);
        boolean created = eventController.createEvent(
            "Entraînement collectif",
            "Entraînement de préparation pour le match de samedi",
            eventDate,
            120, // 2 heures
            "Stade Municipal",
            "TRAINING",
            1, // Club Football
            "coach_zidane"
        );
        
        if (created) {
            System.out.println("✅ Événement créé avec succès");
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 2: Création d'un second événement
        System.out.println("\n--- Test 2: Création d'un second événement ---");
        LocalDateTime matchDate = LocalDateTime.of(2026, 2, 20, 15, 0);
        created = eventController.createEvent(
            "Match amical",
            "Match de préparation contre l'équipe voisine",
            matchDate,
            90,
            "Stade de France",
            "MATCH",
            1,
            "coach_zidane"
        );
        
        if (created) {
            System.out.println("✅ Second événement créé avec succès");
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 3: Création d'un événement pour un autre club
        System.out.println("\n--- Test 3: Événement pour le club Rugby ---");
        LocalDateTime rugbyDate = LocalDateTime.of(2026, 2, 18, 19, 0);
        created = eventController.createEvent(
            "Réunion d'équipe",
            "Discussion tactique avant le match",
            rugbyDate,
            60,
            "Club House",
            "MEETING",
            2, // Club Rugby
            "coach_marie"
        );
        
        if (created) {
            System.out.println("✅ Événement Rugby créé avec succès");
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 4: Récupération des événements du club 1
        System.out.println("\n--- Test 4: Récupération des événements du club 1 ---");
        List<Event> clubEvents = eventController.getEventsByClub(1);
        if (clubEvents != null) {
            System.out.println("✅ " + clubEvents.size() + " événement(s) trouvé(s) pour le club 1");
            for (Event event : clubEvents) {
                System.out.println("  - " + event.getNom() + " le " + event.getDateDebut());
            }
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 5: Récupération des événements créés par coach_zidane
        System.out.println("\n--- Test 5: Événements créés par coach_zidane ---");
        List<Event> coachEvents = eventController.getEventsByCreator("coach_zidane");
        if (coachEvents != null) {
            System.out.println("✅ " + coachEvents.size() + " événement(s) créé(s) par coach_zidane");
            for (Event event : coachEvents) {
                System.out.println("  - " + event.getNom());
            }
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 6: RSVP - Inscription de joueurs
        System.out.println("\n--- Test 6: RSVP - Inscription de joueurs ---");
        if (clubEvents != null && !clubEvents.isEmpty()) {
            int eventId = clubEvents.get(0).getId();
            
            // Joueur 1 confirme sa présence
            boolean rsvp1 = eventController.rsvpToEvent(eventId, "user1", "GOING");
            System.out.println(rsvp1 ? "✅ user1 inscrit (GOING)" : "❌ Erreur: " + eventController.getLastError());
            
            // Joueur 2 confirme sa présence
            boolean rsvp2 = eventController.rsvpToEvent(eventId, "user2", "GOING");
            System.out.println(rsvp2 ? "✅ user2 inscrit (GOING)" : "❌ Erreur: " + eventController.getLastError());
            
            // Joueur 3 hésite
            boolean rsvp3 = eventController.rsvpToEvent(eventId, "user3", "MAYBE");
            System.out.println(rsvp3 ? "✅ user3 inscrit (MAYBE)" : "❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 7: Récupération des participants
        System.out.println("\n--- Test 7: Récupération des participants ---");
        if (clubEvents != null && !clubEvents.isEmpty()) {
            int eventId = clubEvents.get(0).getId();
            Map<String, String> participants = eventController.getEventParticipants(eventId);
            if (participants != null) {
                System.out.println("✅ " + participants.size() + " participant(s) à l'événement");
                for (Map.Entry<String, String> entry : participants.entrySet()) {
                    System.out.println("  - " + entry.getKey() + ": " + entry.getValue());
                }
            } else {
                System.out.println("❌ Erreur: " + eventController.getLastError());
            }
        }
        
        // Test 8: Récupération des événements d'un participant
        System.out.println("\n--- Test 8: Événements auxquels user1 participe ---");
        List<Event> userEvents = eventController.getEventsByParticipant("user1");
        if (userEvents != null) {
            System.out.println("✅ user1 participe à " + userEvents.size() + " événement(s)");
            for (Event event : userEvents) {
                System.out.println("  - " + event.getNom());
            }
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 9: Récupération par plage de dates
        System.out.println("\n--- Test 9: Événements entre le 10 et le 28 février 2026 ---");
        LocalDateTime start = LocalDateTime.of(2026, 2, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 28, 23, 59);
        List<Event> dateRangeEvents = eventController.getEventsByDateRange(start, end);
        if (dateRangeEvents != null) {
            System.out.println("✅ " + dateRangeEvents.size() + " événement(s) dans cette période");
            for (Event event : dateRangeEvents) {
                System.out.println("  - " + event.getNom() + " le " + event.getDateDebut());
            }
        } else {
            System.out.println("❌ Erreur: " + eventController.getLastError());
        }
        
        // Test 10: Mise à jour d'un événement
        System.out.println("\n--- Test 10: Mise à jour d'un événement ---");
        if (clubEvents != null && !clubEvents.isEmpty()) {
            int eventId = clubEvents.get(0).getId();
            LocalDateTime newDate = LocalDateTime.of(2026, 2, 16, 19, 0); // Changement d'horaire
            boolean updated = eventController.updateEvent(
                eventId,
                "Entraînement collectif (MODIFIÉ)",
                "Entraînement intensif de préparation",
                newDate,
                150, // Durée augmentée à 2h30
                "Stade Municipal",
                "TRAINING"
            );
            
            if (updated) {
                System.out.println("✅ Événement mis à jour avec succès");
                Event updatedEvent = eventController.getEventById(eventId);
                if (updatedEvent != null) {
                    System.out.println("  Nouveau nom: " + updatedEvent.getNom());
                    System.out.println("  Nouvelle date: " + updatedEvent.getDateDebut());
                    System.out.println("  Nouvelle durée: " + updatedEvent.getDureeMinutes() + " min");
                }
            } else {
                System.out.println("❌ Erreur: " + eventController.getLastError());
            }
        }
        
        // Test 11: Modification du RSVP
        System.out.println("\n--- Test 11: Modification du RSVP ---");
        if (clubEvents != null && !clubEvents.isEmpty()) {
            int eventId = clubEvents.get(0).getId();
            // user3 change d'avis et confirme sa présence
            boolean rsvpUpdated = eventController.rsvpToEvent(eventId, "user3", "GOING");
            if (rsvpUpdated) {
                System.out.println("✅ RSVP de user3 modifié en GOING");
                Map<String, String> participants = eventController.getEventParticipants(eventId);
                if (participants != null) {
                    System.out.println("  Statut actuel: " + participants.get("user3"));
                }
            } else {
                System.out.println("❌ Erreur: " + eventController.getLastError());
            }
        }
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}
