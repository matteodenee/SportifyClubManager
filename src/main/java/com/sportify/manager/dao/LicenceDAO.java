package com.sportify.manager.dao; // Ton package DAO

import java.util.List;
import com.sportify.manager.services.licence.Licence;
import com.sportify.manager.services.licence.StatutLicence;

public abstract class LicenceDAO {

    public abstract void insert(Licence licence);

    public abstract Licence findById(String id);

    public abstract List<Licence> findByMembre(String membreId);

    public abstract List<Licence> findByStatut(StatutLicence statut);

    public abstract void update(Licence licence);

    public abstract void delete(String id);
}