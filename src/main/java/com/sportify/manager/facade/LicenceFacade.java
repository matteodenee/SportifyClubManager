package com.sportify.manager.facade;

import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.LicenceManager;
import com.sportify.manager.services.licence.StatutLicence;
import java.util.List;

public class LicenceFacade {

    private static LicenceFacade instance = null;

    private LicenceFacade() {
        // Constructeur privé
    }

    public static synchronized LicenceFacade getInstance() {
        if (instance == null) {
            instance = new LicenceFacade();
        }
        return instance;
    }

    // ==========================
    // DEMANDE DE LICENCE (MEMBRE)
    // ==========================
    public void demanderLicence(Licence licence) {
        // On passe par le Manager pour la logique métier
        LicenceManager.getLicenceManager().demanderLicence(licence);
    }

    // ==========================
    // VALIDATION / REFUS (ADMIN)
    // ==========================
    // Cette méthode correspond maintenant exactement à l'appel dans ton Dashboard
    public void validerLicence(String licenceId, boolean accepter, String commentaire) {
        LicenceManager.getLicenceManager().validerLicence(licenceId, accepter, commentaire);
    }

    // ==========================
    // CONSULTATION
    // ==========================
    public Licence getLicenceById(String id) {
        return LicenceManager.getLicenceManager().getLicenceById(id);
    }

    public List<Licence> getLicencesByMembre(String membreId) {
        return LicenceManager.getLicenceManager().getLicencesByMembre(membreId);
    }

    public List<Licence> getLicencesByStatut(StatutLicence statut) {
        return LicenceManager.getLicenceManager().getLicencesByStatut(statut);
    }
}