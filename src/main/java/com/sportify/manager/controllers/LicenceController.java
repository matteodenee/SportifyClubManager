package com.sportify.manager.controllers; // Package corrigé selon ton tree

import com.sportify.manager.services.User;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.frame.MemberDashboardFrame; // Remplace LicenceFrame si besoin
import java.sql.Date;
import java.time.LocalDate;

public class LicenceController {

    private MemberDashboardFrame dashboardFrame; // La vue qui contient le formulaire
    private User currentUser;

    public void setDashboardFrame(MemberDashboardFrame frame) {
        this.dashboardFrame = frame;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Action appelée quand un membre clique sur "Demander Licence"
     */
    public void onDemandeLicence(String sport, TypeLicence type) {

        // 1. Validation des champs
        if (sport == null || type == null) {
            System.out.println("Erreur : Champs vides");
            return;
        }

        if (currentUser == null) {
            System.out.println("Erreur : Utilisateur non connecté");
            return;
        }

        // 2. Création de l'objet Licence avec les bons paramètres
        // Selon ton constructeur dans Licence.java
        Licence licence = new Licence(
                Licence.createidlicence(),      // Génération ID
                sport,
                type,
                StatutLicence.EN_ATTENTE,        // Statut initial
                Date.valueOf(LocalDate.now()),  // Date de demande
                null,                           // Date début (sera mis par le Directeur)
                null,                           // Date fin (sera mis par le Directeur)
                currentUser,                    // Le membre connecté
                null,                           // Documents
                null,                           // Date décision
                ""                              // Commentaire admin vide
        );

        // 3. Appel à la Facade (Singleton)
        LicenceFacade facade = LicenceFacade.getInstance(); // Utilisation du nom conventionnel
        facade.demanderLicence(licence);

        // 4. Feedback (à adapter selon tes méthodes dans la Frame)
        System.out.println("Demande de licence envoyée avec succès !");
    }
}