package com.sportify.manager.services;

import java.time.LocalDate;
import java.time.LocalTime;

public class Entrainement {
    private Integer id;
    private LocalDate date;
    private LocalTime heure;
    private String lieu;
    private String activite;
    private int clubId;

    public Entrainement(Integer id, LocalDate date, LocalTime heure, String lieu, String activite, int clubId) {
        this.id = id;
        this.date = date;
        this.heure = heure;
        this.lieu = lieu;
        this.activite = activite;
        this.clubId = clubId;
    }

    public Entrainement(LocalDate date, LocalTime heure, String lieu, String activite, int clubId) {
        this(null, date, heure, lieu, activite, clubId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
}
