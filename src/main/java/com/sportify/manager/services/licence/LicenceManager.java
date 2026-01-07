package com.sportify.manager.services.licence;

import java.util.List;
import com.sportify.manager.dao.LicenceDAO;
import com.sportify.manager.persistence.AbstractFactory;

public class LicenceManager {

    private static LicenceManager instance = null;

    private LicenceManager() {
        // Constructeur privé pour le Singleton
    }

    // Implémentation du Singleton
    public static synchronized LicenceManager getLicenceManager() {
        if (instance == null) {
            instance = new LicenceManager();
        }
        return instance;
    }

    // PERSISTANCE : Enregistre une nouvelle demande
    public void demanderLicence(Licence licence) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createLicenceDAO(); // Nom corrigé
        licencedao.insert(licence);
    }

    // BUSINESS LOGIC : Traitement par le directeur
    public void validerLicence(String licenceId, boolean accepter, String commentaire) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createLicenceDAO();

        Licence licence = licencedao.findById(licenceId);
        if (licence != null) {
            licence.setCommentaireAdmin(commentaire);

            if (accepter) {
                licence.setStatut(StatutLicence.ACTIVE);
            } else {
                licence.setStatut(StatutLicence.REFUSEE);
            }

            // On n'oublie pas de sauvegarder la modification en base !
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