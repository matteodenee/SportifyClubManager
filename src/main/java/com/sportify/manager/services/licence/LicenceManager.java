package com.sportify.manager.services.licence;

import java.util.List;
import com.sportify.manager.dao.LicenceDAO;
import com.sportify.manager.persistence.AbstractFactory;

public class LicenceManager {

    private static LicenceManager instance = null;

    private LicenceManager() {
        // Constructeur privé pour le Singleton
    }

    public static synchronized LicenceManager getLicenceManager() {
        if (instance == null) {
            instance = new LicenceManager();
        }
        return instance;
    }

    /**
     * PERSISTANCE : Enregistre une nouvelle demande.
     * Note : L'objet licence contient maintenant un TypeSport complet.
     */
    public void demanderLicence(Licence licence) {
        // On pourrait ajouter ici une vérification métier (ex: le membre a-t-il déjà une licence active pour ce sport ?)
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createLicenceDAO();
        licencedao.insert(licence);
    }

    /**
     * BUSINESS LOGIC : Traitement par le directeur ou l'admin.
     */
    public void validerLicence(String licenceId, boolean accepter, String commentaire) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createLicenceDAO();

        Licence licence = licencedao.findById(licenceId);
        if (licence != null) {
            licence.setCommentaireAdmin(commentaire);
            licence.setDateDecision(new java.sql.Date(System.currentTimeMillis()));

            if (accepter) {
                licence.setStatut(StatutLicence.ACTIVE);
                // On pourrait ici initialiser les dates de début et fin par défaut
                licence.setDateDebut(new java.sql.Date(System.currentTimeMillis()));
            } else {
                licence.setStatut(StatutLicence.REFUSEE);
            }

            licencedao.update(licence);
        }
    }

    // CONSULTATION
    public Licence getLicenceById(String id) {
        return AbstractFactory.getFactory().createLicenceDAO().findById(id);
    }

    public List<Licence> getLicencesByMembre(String membreId) {
        return AbstractFactory.getFactory().createLicenceDAO().findByMembre(membreId);
    }

    public List<Licence> getLicencesByStatut(StatutLicence statut) {
        return AbstractFactory.getFactory().createLicenceDAO().findByStatut(statut);
    }
}