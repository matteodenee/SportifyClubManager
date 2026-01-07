package com.sportify.manager.services.licence;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;
import com.sportify.manager.services.User;
import com.sportify.manager.services.TypeSport; // Import de la classe de ton ami

public class Licence {
    private String id;
    private TypeSport sport; // CHANGÉ : String -> TypeSport
    private TypeLicence typeLicence;
    private StatutLicence statut;
    private Date dateDemande;
    private Date dateDebut;
    private Date dateFin;
    private User membre;
    private Document[] lisDocument;
    private Date dateDecision;
    private String commentaireAdmin;

    // Constructeur complet mis à jour
    public Licence(String id, TypeSport sport, TypeLicence typeLicence, StatutLicence statut,
                   Date dateDemande, Date dateDebut, Date dateFin, User membre,
                   Document[] lisDocument, Date dateDecision, String commentaireAdmin) {
        this.id = id;
        this.sport = sport; // Reçoit l'objet TypeSport
        this.typeLicence = typeLicence;
        this.statut = statut;
        this.dateDemande = dateDemande;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.membre = membre;
        this.lisDocument = lisDocument;
        this.dateDecision = dateDecision;
        this.commentaireAdmin = commentaireAdmin;
    }

    // --- GETTERS ---
    public String getId() { return id; }
    public TypeSport getSport() { return sport; } // Retourne l'objet complet
    public TypeLicence getTypeLicence() { return typeLicence; }
    public StatutLicence getStatut() { return statut; }
    public Date getDateDemande() { return dateDemande; }
    public Date getDateDebut() { return dateDebut; }
    public Date getDateFin() { return dateFin; }
    public User getMembre() { return membre; }
    public Document[] getLisDocument() { return lisDocument; }
    public Date getDateDecision() { return dateDecision; }
    public String getCommentaireAdmin() { return commentaireAdmin; }

    // --- SETTERS ---
    public void setSport(TypeSport sport) { this.sport = sport; }
    public void setStatut(StatutLicence statut) { this.statut = statut; }
    public void setCommentaireAdmin(String commentaireAdmin) { this.commentaireAdmin = commentaireAdmin; }
    public void setDateDecision(Date dateDecision) { this.dateDecision = dateDecision; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    // --- UTILITAIRES ---
    public static String createidlicence() {
        return UUID.randomUUID().toString();
    }

    public boolean estExpiree() {
        return dateFin != null && dateFin.before(Date.valueOf(LocalDate.now()));
    }
}