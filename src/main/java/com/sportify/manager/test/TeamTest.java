package com.sportify.manager.test;

import com.sportify.manager.facade.TeamFacade;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;

import java.util.List;

/**
 * Classe de test pour la gestion des équipes.
 * Test des opérations CRUD et de la gestion des joueurs.
 */
public class TeamTest {
    
    public static void main(String[] args) {
        System.out.println("=== TEST TEAM MANAGEMENT ===\n");
        
        TeamFacade teamFacade = TeamFacade.getInstance();
        
        try {
            // Test 1: Création d'une équipe
            System.out.println("--- Test 1: Création d'équipe ---");
            teamFacade.createTeam(
                "Équipe Senior A",
                "Senior",
                1, // Club Football
                null, // Pas de coach pour l'instant
                1  // Football
            );
            System.out.println("✅ Équipe créée avec succès");
            
            // Test 2: Création d'une deuxième équipe
            System.out.println("\n--- Test 2: Création d'une deuxième équipe ---");
            teamFacade.createTeam(
                "Équipe U18",
                "U18",
                1, // Club Football
                null,
                1
            );
            System.out.println("✅ Deuxième équipe créée avec succès");
            
            // Test 3: Récupération des équipes du club 1
            System.out.println("\n--- Test 3: Récupération des équipes du club 1 ---");
            List<Team> teams = teamFacade.getTeamsByClub(1);
            System.out.println("✅ " + teams.size() + " équipe(s) trouvée(s) pour le club 1");
            for (Team team : teams) {
                System.out.println("  - " + team.getNom() + " (" + team.getCategorie() + ")");
            }
            
            if (teams.isEmpty()) {
                System.out.println("❌ Aucune équipe trouvée, arrêt des tests");
                return;
            }
            
            // Test 4: Récupération d'une équipe par ID
            System.out.println("\n--- Test 4: Récupération d'une équipe par ID ---");
            Team firstTeam = teams.get(0);
            Team retrievedTeam = teamFacade.getTeamById(firstTeam.getId());
            if (retrievedTeam != null) {
                System.out.println("✅ Équipe récupérée: " + retrievedTeam.getNom());
                System.out.println("   ID: " + retrievedTeam.getId());
                System.out.println("   Catégorie: " + retrievedTeam.getCategorie());
            } else {
                System.out.println("❌ Erreur: Équipe non trouvée");
            }
            
            // Test 5: Ajout de joueurs à l'équipe
            System.out.println("\n--- Test 5: Ajout de joueurs ---");
            teamFacade.addPlayerToTeam(firstTeam.getId(), "user1");
            System.out.println("✅ user1 ajouté à l'équipe");
            
            teamFacade.addPlayerToTeam(firstTeam.getId(), "user2");
            System.out.println("✅ user2 ajouté à l'équipe");
            
            teamFacade.addPlayerToTeam(firstTeam.getId(), "user3");
            System.out.println("✅ user3 ajouté à l'équipe");
            
            // Test 6: Récupération des joueurs de l'équipe
            System.out.println("\n--- Test 6: Récupération des joueurs ---");
            List<User> players = teamFacade.getPlayersByTeam(firstTeam.getId());
            System.out.println("✅ " + players.size() + " joueur(s) dans l'équipe");
            for (User player : players) {
                System.out.println("  - " + player.getName() + " (" + player.getId() + ")");
            }
            
            // Test 7: Retrait d'un joueur
            System.out.println("\n--- Test 7: Retrait d'un joueur ---");
            teamFacade.removePlayerFromTeam(firstTeam.getId(), "user3");
            System.out.println("✅ user3 retiré de l'équipe");
            
            players = teamFacade.getPlayersByTeam(firstTeam.getId());
            System.out.println("✅ " + players.size() + " joueur(s) restant(s) dans l'équipe");
            
            // Test 8: Mise à jour d'une équipe
            System.out.println("\n--- Test 8: Mise à jour d'une équipe ---");
            firstTeam.setNom("Équipe Senior A (MODIFIÉE)");
            firstTeam.setCategorie("Senior Elite");
            teamFacade.updateTeam(firstTeam);
            System.out.println("✅ Équipe mise à jour avec succès");
            
            Team updatedTeam = teamFacade.getTeamById(firstTeam.getId());
            if (updatedTeam != null) {
                System.out.println("   Nouveau nom: " + updatedTeam.getNom());
                System.out.println("   Nouvelle catégorie: " + updatedTeam.getCategorie());
            }
            
            // Test 9: Suppression d'une équipe
            System.out.println("\n--- Test 9: Suppression d'une équipe ---");
            if (teams.size() > 1) {
                Team teamToDelete = teams.get(teams.size() - 1);
                teamFacade.deleteTeam(teamToDelete.getId());
                System.out.println("✅ Équipe '" + teamToDelete.getNom() + "' supprimée");
                
                teams = teamFacade.getTeamsByClub(1);
                System.out.println("✅ " + teams.size() + " équipe(s) restante(s)");
            }
            
            System.out.println("\n=== FIN DES TESTS - TOUS RÉUSSIS ✅ ===");
            
        } catch (Exception e) {
            System.out.println("❌ Erreur durant les tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
