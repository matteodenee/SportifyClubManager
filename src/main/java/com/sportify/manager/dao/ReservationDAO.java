package com.sportify.manager.dao;

import com.sportify.manager.services.Reservation;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
    boolean insert(Reservation reservation);
    List<Reservation> findOverlapping(int equipmentId, LocalDate start, LocalDate end);
}
