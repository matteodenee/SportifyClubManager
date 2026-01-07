package com.sportify.manager.controllers;

import com.sportify.manager.services.User;
import com.sportify.manager.services.TypeSport; // Ajout de l'import TypeSport
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.frame.MemberDashboardFrame;
import java.sql.Date;
import java.time.LocalDate;

public class LicenceController {

    private MemberDashboardFrame dashboardFrame;
    private User currentUser;

    public void setDashboardFrame(MemberDashboardFrame frame) {
        this.dashboardFrame = frame;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Action mise à jour : Reçoit maintenant un TypeSport au lieu d'un String
     */
    public void onDemandeLicence(TypeSport sport, TypeLicence type) {

        // 1. Validation des objets (on vérifie que l'objet sport n'est pas nul)
        if (sport == null || type == null) {
            System.out.println("Erreur : Veuillez sélectionner un sport et un type de licence.");
            return;
        }

        if (currentUser == null) {
            System.out.println("Erreur : Utilisateur non connecté");
            return;
        }

        // 2. Création de l'objet Licence
        // Note : On passe l'objet 'sport' (TypeSport) directement au constructeur
        Licence licence = new Licence(
                Licence.createidlicence(),
                sport,                          // Objet TypeSport complet
                type,
                StatutLicence.EN_ATTENTE,
                Date.valueOf(LocalDate.now()),
                null,
                null,
                currentUser,
                null,
                null,
                ""
        );

        // 3. Transmission à la Facade
        LicenceFacade facade = LicenceFacade.getInstance();
        facade.demanderLicence(licence);

        System.out.println("Demande de licence pour le sport " + sport.getNom() + " envoyée avec succès !");
    }
}