package com.sportify.manager.test;

import com.sportify.manager.facade.TeamFacade;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;

import java.util.List;

/**
 * Test simple pour vérifier que getPlayersByTeam() fonctionne correctement.
 */
public class TeamPlayerTest {
    
    public static void main(String[] args) {
        try {
            TeamFacade teamFacade = TeamFacade.getInstance();
            
            System.out.println("=== TEST: getPlayersByTeam() avec objets User ===\n");
            
            // Récupérer les équipes du club 1
            List<Team> teams = teamFacade.getTeamsByClub(1);
            
            if (teams.isEmpty()) {
                System.out.println("❌ Aucune équipe trouvée pour le club 1");
                return;
            }
            
            Team firstTeam = teams.get(0);
            System.out.println("Équipe testée: " + firstTeam.getNom());
            System.out.println("ID de l'équipe: " + firstTeam.getId());
            
            // Tester getPlayersByTeam() - devrait retourner des objets User
            System.out.println("\n--- Test: Récupération des joueurs (objets User) ---");
            List<User> players = teamFacade.getPlayersByTeam(firstTeam.getId());
            
            if (players == null) {
                System.out.println("❌ getPlayersByTeam() a retourné null");
                return;
            }
            
            System.out.println("✅ Nombre de joueurs: " + players.size());
            
            if (players.isEmpty()) {
                System.out.println("⚠️  L'équipe n'a pas de joueurs");
            } else {
                System.out.println("\nDétails des joueurs:");
                for (User player : players) {
                    System.out.println("  - ID: " + player.getId());
                    System.out.println("    Nom: " + player.getName());
                    System.out.println("    Email: " + player.getEmail());
                    System.out.println("    Rôle: " + player.getRole());
                    System.out.println();
                }
            }
            
            System.out.println("✅ TEST RÉUSSI - getPlayersByTeam() retourne bien des objets User");
            
        } catch (Exception e) {
            System.out.println("❌ Erreur durant le test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
