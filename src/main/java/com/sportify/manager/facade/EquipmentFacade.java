package com.sportify.manager.facade;

import com.sportify.manager.services.Equipment;
import com.sportify.manager.services.EquipmentManager;
import com.sportify.manager.services.Reservation;
import java.util.List;

public class EquipmentFacade {
    private static EquipmentFacade instance;
    private final EquipmentManager manager;

    private EquipmentFacade() {
        this.manager = EquipmentManager.getInstance();
    }

    public static synchronized EquipmentFacade getInstance() {
        if (instance == null) {
            instance = new EquipmentFacade();
        }
        return instance;
    }

    public boolean addEquipment(Equipment equipment) {
        return manager.processNewEquipment(equipment);
    }

    public boolean createReservation(Reservation reservation) {
        return manager.processReservation(reservation);
    }

    public List<Equipment> getAllEquipment() {
        return manager.getAllEquipment();
    }

    public List<Reservation> getReservationsByUser(String userId) {
        return manager.getReservationsByUser(userId);
    }

    public List<Reservation> getReservationsByClub(int clubId) {
        return manager.getReservationsByClub(clubId);
    }

    public boolean updateReservationStatus(int reservationId, String status) {
        return manager.updateReservationStatus(reservationId, status);
    }
}
