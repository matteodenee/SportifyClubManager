package licenceManagement.Bl;

import java.sql.Date;

import TypeSportManagement.TypeSport;
import UserManagent.User;
import licenceManagement.Enum.Document;
import licenceManagement.Enum.StatutLicence;
import licenceManagement.Enum.TypeLicence;

import java.time.LocalDate;
import java.util.UUID;

public class Licence {

    private String id;
    private TypeSport sport;
    private TypeLicence typeLicence;
    private StatutLicence statut;
    private Date dateDemande;
    private Date dateDebut;
    private Date dateFin;
    private User membre;
    private Document[] lisDocument;
    private Date dateDecision;
    private String commentaireAdmin;

    public Licence(
            TypeSport sport,
            TypeLicence typeLicence,
            Date dateDebut,
            Date dateFin,
            User membre,
            Document[] lisDocument) {
        this.id = createidlicence();
        this.sport = sport;
        this.typeLicence = typeLicence;
        this.statut = StatutLicence.EN_ATTENTE;
        this.dateDemande = Date.valueOf(LocalDate.now());
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.membre = membre;
        this.lisDocument = lisDocument;
        this.dateDecision = null;
        this.commentaireAdmin = null;
    }

    // Constructeur de reconstruction depuis la base de données
    public Licence(
            String id,
            TypeSport sport,
            TypeLicence typeLicence,
            StatutLicence statut,
            Date dateDemande,
            Date dateDebut,
            Date dateFin,
            User membre,
            Document[] lisDocument,
            Date dateDecision,
            String commentaireAdmin
    ) {
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



    public String getId() {
        return id;
    }

    public TypeSport getSport() {
        return sport;
    }

    public TypeLicence getTypeLicence() {
        return typeLicence;
    }

    public StatutLicence getStatut() {
        return statut;
    }

    public Date getDateDemande() {
        return dateDemande;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public User getMembre() {
        return membre;
    }

    public Document[] getLisDocument() {
        return lisDocument;
    }

    public Date getDateDecision() {
        return dateDecision;
    }

    public String getCommentaireAdmin() {
        return commentaireAdmin;
    }
    public String createidlicence(){
        return UUID.randomUUID().toString();
    }

    void setStatut(StatutLicence statut) {
    this.statut = statut;
    }

    void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    void setDateDecision(Date dateDecision) {
        this.dateDecision = dateDecision;
    }

    void setCommentaireAdmin(String commentaireAdmin) {
        this.commentaireAdmin = commentaireAdmin;
    }

    public boolean estExpiree() {
    return dateFin != null && dateFin.before(Date.valueOf(LocalDate.now()));
}


}

