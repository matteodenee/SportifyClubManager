package licenceManagement.Bl;

import java.util.List;


import licenceManagement.Bl.dao.LicenceDAO;
import licenceManagement.Enum.StatutLicence;
import persistance.AbstractFactory;

public class LicenceManager {

    public Licence validerlicence(Licence licence){
        return licence;
    }


    public static LicenceManager getLicenceManager() {
             
        throw new UnsupportedOperationException("Unimplemented method 'getLicenceManager'");
    }

    public void validerLicence(String licenceId, boolean accepter, String commentaire) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createlicenceDAO();
        Licence licence = licencedao.findById(licenceId);
        licence.setCommentaireAdmin(commentaire); 
        if (accepter) {
            licence.setStatut(StatutLicence.ACTIVE); 
        }
        else if (licence.estExpiree()){
            licence.setStatut(StatutLicence.EXPIREE); 
        }
        else {
            licence.setStatut(StatutLicence.REFUSEE);
        }
    }

    public Licence getLicenceById(String id) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createlicenceDAO();
        Licence licence = licencedao.findById(id);
        return licence;
    }

    public List<Licence> getLicencesByMembre(String membreId) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createlicenceDAO();
        List<Licence> licences = licencedao.findByMembre(membreId);
        return licences;
    }

    public List<Licence> getLicencesByStatut(StatutLicence statut) {
        AbstractFactory f = AbstractFactory.getFactory();
        LicenceDAO licencedao = f.createlicenceDAO();
        List<Licence> licences = licencedao.findByStatut(statut);
        return licences;
    }
    
}
