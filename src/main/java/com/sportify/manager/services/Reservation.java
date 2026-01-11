package com.sportify.manager.services;

import java.time.LocalDate;

public class Reservation {
    private int id;
    private int equipmentId;
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public Reservation(int id, int equipmentId, String userId, LocalDate startDate, LocalDate endDate, String status) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Reservation(int equipmentId, String userId, LocalDate startDate, LocalDate endDate, String status) {
        this(0, equipmentId, userId, startDate, endDate, status);
    }

    public int getId() { return id; }
    public int getEquipmentId() { return equipmentId; }
    public String getUserId() { return userId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }
}
