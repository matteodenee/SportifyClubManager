package licenceManagement.Bl;

import java.util.List;

import licenceManagement.Enum.StatutLicence;

public abstract class LicenceDAO {

    public abstract void save(Licence licence);

    public abstract Licence findById(String id);

    public abstract List<Licence> findByMembre(String membreId);

    public abstract List<Licence> findByStatut(StatutLicence statut);

    public abstract void update(Licence licence);

    public abstract void delete(String id);
}
