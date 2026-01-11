package com.sportify.manager.dao;

import com.sportify.manager.services.Reservation;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
    boolean insert(Reservation reservation);
    List<Reservation> findOverlapping(int equipmentId, LocalDate start, LocalDate end);
    List<Reservation> listByUser(String userId);
    List<Reservation> listByClub(int clubId);
    boolean updateStatus(int reservationId, String status);
    int countOverlapping(int equipmentId, LocalDate start, LocalDate end);
}
