package com.sportify.manager.services.licence;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;
import com.sportify.manager.services.User;

public class Licence {
    private String id;
    private String sport;
    private TypeLicence typeLicence;
    private StatutLicence statut;
    private Date dateDemande;
    private Date dateDebut;
    private Date dateFin;
    private User membre;
    private Document[] lisDocument;
    private Date dateDecision;
    private String commentaireAdmin;

    // Constructeur complet
    public Licence(String id, String sport, TypeLicence typeLicence, StatutLicence statut,
                   Date dateDemande, Date dateDebut, Date dateFin, User membre,
                   Document[] lisDocument, Date dateDecision, String commentaireAdmin) {
        this.id = id;
        this.sport = sport;
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

    // --- GETTERS (Pour lire les données) ---

    public String getId() { return id; }
    public String getSport() { return sport; }
    public TypeLicence getTypeLicence() { return typeLicence; }
    public StatutLicence getStatut() { return statut; }
    public Date getDateDemande() { return dateDemande; }
    public Date getDateDebut() { return dateDebut; }
    public Date getDateFin() { return dateFin; }
    public User getMembre() { return membre; }
    public Document[] getLisDocument() { return lisDocument; }
    public Date getDateDecision() { return dateDecision; }
    public String getCommentaireAdmin() { return commentaireAdmin; }

    // --- SETTERS (Pour modifier les données - CORRIGÉ) ---

    public void setId(String id) { this.id = id; }

    public void setStatut(StatutLicence statut) {
        this.statut = statut;
    }

    public void setCommentaireAdmin(String commentaireAdmin) {
        this.commentaireAdmin = commentaireAdmin;
    }

    public void setDateDecision(Date dateDecision) {
        this.dateDecision = dateDecision;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public void setTypeLicence(TypeLicence typeLicence) {
        this.typeLicence = typeLicence;
    }

    // --- UTILITAIRES ---

    public static String createidlicence() {
        return UUID.randomUUID().toString();
    }

    public boolean estExpiree() {
        return dateFin != null && dateFin.before(Date.valueOf(LocalDate.now()));
    }
}