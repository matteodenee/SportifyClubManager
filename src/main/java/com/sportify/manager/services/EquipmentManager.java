package com.sportify.manager.services;

import com.sportify.manager.dao.EquipmentDAO;
import com.sportify.manager.dao.ReservationDAO;
import com.sportify.manager.persistence.AbstractFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class EquipmentManager {
    private static EquipmentManager instance;
    private final EquipmentDAO equipmentDAO;
    private final ReservationDAO reservationDAO;

    private EquipmentManager() {
        AbstractFactory factory = AbstractFactory.getFactory();
        this.equipmentDAO = factory.createEquipmentDAO();
        this.reservationDAO = factory.createReservationDAO();
    }

    public static synchronized EquipmentManager getInstance() {
        if (instance == null) {
            instance = new EquipmentManager();
        }
        return instance;
    }

    public boolean processNewEquipment(Equipment equipment) {
        if (equipment == null) {
            return false;
        }
        if (isBlank(equipment.getName()) || isBlank(equipment.getType()) || isBlank(equipment.getCondition())) {
            return false;
        }
        if (equipment.getQuantity() <= 0) {
            return false;
        }
        if (equipment.getClubId() <= 0) {
            return false;
        }
        if (equipmentDAO.findByName(equipment.getName()) != null) {
            return false;
        }
        return equipmentDAO.insert(equipment);
    }

    public boolean processReservation(Reservation reservation) {
        if (reservation == null) {
            return false;
        }
        if (reservation.getEquipmentId() <= 0 || isBlank(reservation.getUserId())) {
            return false;
        }
        if (reservation.getStartDate() == null || reservation.getEndDate() == null) {
            return false;
        }
        if (reservation.getStartDate().isAfter(reservation.getEndDate())) {
            return false;
        }
        if (!checkAvailability(reservation.getEquipmentId(), reservation.getStartDate(), reservation.getEndDate())) {
            return false;
        }
        return reservationDAO.insert(reservation);
    }

    public boolean checkAvailability(int equipmentId, LocalDate start, LocalDate end) {
        if (equipmentId <= 0 || start == null || end == null) {
            return false;
        }
        Equipment equipment = equipmentDAO.findById(equipmentId);
        if (equipment == null) {
            return false;
        }
        int reserved = reservationDAO.countOverlapping(equipmentId, start, end);
        return reserved < equipment.getQuantity();
    }

    public List<Equipment> getAllEquipment() {
        List<Equipment> list = equipmentDAO.listAll();
        return list != null ? list : Collections.emptyList();
    }

    public List<Reservation> getReservationsByUser(String userId) {
        List<Reservation> list = reservationDAO.listByUser(userId);
        return list != null ? list : Collections.emptyList();
    }

    public List<Reservation> getReservationsByClub(int clubId) {
        List<Reservation> list = reservationDAO.listByClub(clubId);
        return list != null ? list : Collections.emptyList();
    }

    public boolean updateReservationStatus(int reservationId, String status) {
        if (reservationId <= 0 || isBlank(status)) {
            return false;
        }
        return reservationDAO.updateStatus(reservationId, status.trim().toUpperCase());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
