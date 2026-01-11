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
        List<Reservation> conflicts = reservationDAO.findOverlapping(equipmentId, start, end);
        return conflicts == null || conflicts.isEmpty();
    }

    public List<Equipment> getAllEquipment() {
        List<Equipment> list = equipmentDAO.listAll();
        return list != null ? list : Collections.emptyList();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
