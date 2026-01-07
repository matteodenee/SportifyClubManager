package licenceManagement.Bl.facade;


import java.util.List;

import licenceManagement.Bl.Licence;
import licenceManagement.Bl.LicenceManager;
import licenceManagement.Enum.StatutLicence;

public class LicenceFacade {

    private static LicenceFacade instance = null;

    private LicenceFacade() {
    }

    public static LicenceFacade createLicenceFacade() {
        if (instance == null) {
            instance = new LicenceFacade();
        }
        return instance;
    }

    // ==========================
    // DEMANDE DE LICENCE (MEMBRE)
    // ==========================
    public void demanderLicence(Licence licence) {
        LicenceManager lm = LicenceManager.getLicenceManager();
        lm.validerlicence(licence);
    }

    // ==========================
    // VALIDATION / REFUS (ADMIN)
    // ==========================
    public void validerLicence(String licenceId, boolean accepter, String commentaire) {
        LicenceManager lm = LicenceManager.getLicenceManager();
        lm.validerLicence(licenceId, accepter, commentaire);
    }

    // ==========================
    // CONSULTATION
    // ==========================
    public Licence getLicenceById(String id) {
        LicenceManager lm = LicenceManager.getLicenceManager();
        return lm.getLicenceById(id);
    }

    public List<Licence> getLicencesByMembre(String membreId) {
        LicenceManager lm = LicenceManager.getLicenceManager();
        return lm.getLicencesByMembre(membreId);
    }

    public List<Licence> getLicencesByStatut(StatutLicence statut) {
        LicenceManager lm = LicenceManager.getLicenceManager();
        return lm.getLicencesByStatut(statut);
    }
}
