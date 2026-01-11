package com.sportify.manager.controllers;

import com.sportify.manager.facade.EquipmentFacade;
import com.sportify.manager.services.Equipment;
import com.sportify.manager.services.Reservation;
import java.time.LocalDate;
import java.util.List;

public class EquipmentController {
    private final EquipmentFacade facade = EquipmentFacade.getInstance();
    private String lastError;

    public boolean handleCreateEquipment(String name, String type, String condition, int qty) {
        if (name == null || name.trim().isEmpty()) {
            lastError = "Nom obligatoire.";
            return false;
        }
        if (type == null || type.trim().isEmpty()) {
            lastError = "Type obligatoire.";
            return false;
        }
        if (condition == null || condition.trim().isEmpty()) {
            lastError = "Etat obligatoire.";
            return false;
        }
        if (qty <= 0) {
            lastError = "Quantite invalide.";
            return false;
        }

        boolean ok = facade.addEquipment(new Equipment(name.trim(), type.trim(), condition.trim(), qty));
        lastError = ok ? null : "Ajout impossible.";
        return ok;
    }

    public boolean handleReserveEquipment(int equipId, String userId, LocalDate startDate, LocalDate endDate) {
        if (equipId <= 0) {
            lastError = "Equipement invalide.";
            return false;
        }
        if (userId == null || userId.trim().isEmpty()) {
            lastError = "Utilisateur invalide.";
            return false;
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            lastError = "Dates invalides.";
            return false;
        }

        Reservation reservation = new Reservation(equipId, userId.trim(), startDate, endDate, "PENDING");
        boolean ok = facade.createReservation(reservation);
        lastError = ok ? null : "Reservation impossible.";
        return ok;
    }

    public List<Equipment> handleViewAllEquipment() {
        return facade.getAllEquipment();
    }

    public String getLastError() {
        return lastError;
    }
}
