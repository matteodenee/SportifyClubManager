package com.sportify.manager.services;

import java.time.LocalDate;
import java.time.LocalTime;

public class Training {
    private int id;
    private LocalDate date;
    private LocalTime heure;
    private String lieu;
    private String activite;
    private int clubId;

    public Training(int id, LocalDate date, LocalTime heure, String lieu, String activite, int clubId) {
        this.id = id;
        this.date = date;
        this.heure = heure;
        this.lieu = lieu;
        this.activite = activite;
        this.clubId = clubId;
    }

    public Training(LocalDate date, LocalTime heure, String lieu, String activite, int clubId) {
        this(0, date, heure, lieu, activite, clubId);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public LocalTime getHeure() { return heure; }
    public String getLieu() { return lieu; }
    public String getActivite() { return activite; }
    public int getClubId() { return clubId; }
}
