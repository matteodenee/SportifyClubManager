package com.sportify.manager.controllers;

import com.sportify.manager.services.User;
import com.sportify.manager.services.TypeSport;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.TypeLicence;
import com.sportify.manager.services.licence.StatutLicence;
import com.sportify.manager.facade.LicenceFacade;
import com.sportify.manager.frame.MemberDashboardFrame;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

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
     * Action appelée par le MemberDashboardFrame (Critère 7.1)
     * Utilisation de 'throws Exception' pour propager les règles métier (A1, A2) vers l'UI.
     */
    public void onDemandeLicence(TypeSport sport, TypeLicence type) throws Exception {

        if (sport == null || type == null) {
            throw new Exception("Veuillez sélectionner un sport et un type de licence.");
        }

        if (currentUser == null) {
            throw new Exception("Utilisateur non connecté.");
        }

        // Création de l'objet Licence
        Licence licence = new Licence(
                Licence.createidlicence(),
                sport,
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

        // Transmission à la Facade (qui appelle le Manager)
        LicenceFacade.getInstance().demanderLicence(licence);

        System.out.println("Demande de licence pour le sport " + sport.getNom() + " envoyée avec succès !");
    }

    /**
     * MÉTHODE CORRIGÉE : Apposée pour résoudre l'erreur "cannot find symbol"
     * Action appelée par le ClubManagementFrame (Critère 7.2)
     */
    public void validerLicence(String licenceId, boolean accepter, String commentaire) {
        // On délègue à la Facade qui s'occupe de la logique métier via le Manager
        LicenceFacade.getInstance().validerLicence(licenceId, accepter, commentaire);
    }

    /**
     * Récupère les licences par statut pour l'affichage Admin (Critère 7.2)
     */
    public List<Licence> getLicencesByStatut(StatutLicence statut) {
        return LicenceFacade.getInstance().getLicencesByStatut(statut);
    }

    /**
     * Récupère les licences d'un membre pour son historique (Critère 7.4)
     */
    public List<Licence> getLicencesByMembre(String membreId) {
        return LicenceFacade.getInstance().getLicencesByMembre(membreId);
    }
}