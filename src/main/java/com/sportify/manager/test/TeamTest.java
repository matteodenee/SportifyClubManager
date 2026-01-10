package com.sportify.manager.test;

import com.sportify.manager.controllers.TeamController;
import com.sportify.manager.services.Team;
import com.sportify.manager.services.User;
import java.util.List;

/**
 * Classe de test pour vérifier que la fonctionnalité Team fonctionne correctement
 * ATTENTION : Ceci est un test temporaire, pas pour la production !
 */
public class TeamTest {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE LA FONCTIONNALITÉ TEAM ===\n");
        
        // IMPORTANT : Initialiser la connexion PostgreSQL d'abord
        System.out.println("Initialisation de la connexion à la base de données...");
        com.sportify.manager.dao.PostgresUserDAO.getInstance();
        System.out.println("✅ Connexion initialisée\n");
        
        TeamController controller = TeamController.getInstance();
        
        // Test 1 : Créer une équipe
        System.out.println("1. Création d'une équipe...");
        boolean created = controller.handleCreateTeam(
            "Équipe Senior A",  // nom
            "Senior",           // catégorie
            1,                  // clubId (assurez-vous qu'un club avec ID=1 existe)
            null,              // coachId (optionnel)
            1                  // typeSportId (optionnel, assurez-vous qu'il existe)
        );
        
        if (created) {
            System.out.println("✅ Équipe créée avec succès !\n");
        } else {
            System.out.println("❌ Erreur : " + controller.getLastError() + "\n");
            return;
        }
        
        // Test 2 : Récupérer les équipes du club
        System.out.println("2. Récupération des équipes du club 1...");
        List<Team> teams = controller.handleGetTeams(1);
        
        if (teams != null) {
            System.out.println("✅ Nombre d'équipes trouvées : " + teams.size());
            for (Team team : teams) {
                System.out.println("   - " + team.getNom() + " (" + team.getCategorie() + ")");
            }
            System.out.println();
        } else {
            System.out.println("❌ Erreur : " + controller.getLastError() + "\n");
            return;
        }
        
        // Test 3 : Ajouter un joueur (si vous avez un user avec ID "user1")
        if (!teams.isEmpty()) {
            Team firstTeam = teams.get(0);
            System.out.println("3. Ajout d'un joueur à l'équipe " + firstTeam.getNom() + "...");
            
            boolean playerAdded = controller.handleAddPlayer(firstTeam.getId(), "user1");
            if (playerAdded) {
                System.out.println("✅ Joueur ajouté avec succès !\n");
            } else {
                System.out.println("⚠️  Avertissement : " + controller.getLastError() + "\n");
            }
            
            // Test 4 : Récupérer les joueurs de l'équipe
            System.out.println("4. Récupération des joueurs de l'équipe...");
            List<User> players = controller.handleGetPlayers(firstTeam.getId());
            
            if (players != null) {
                System.out.println("✅ Nombre de joueurs : " + players.size());
                for (User player : players) {
                    System.out.println("   - " + player.getName() + " (" + player.getId() + ")");
                }
                System.out.println();
            } else {
                System.out.println("⚠️  Pas de joueurs ou erreur : " + controller.getLastError() + "\n");
            }
        }
        
        System.out.println("=== FIN DES TESTS ===");
    }
}
