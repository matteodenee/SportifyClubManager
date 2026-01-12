package com.sportify.manager.services;


public class RoleAssignment {
    private final String role;
    private final int slotIndex;
    private final String playerId;

    public RoleAssignment(String role, int slotIndex, String playerId) {
        this.role = role;
        this.slotIndex = slotIndex;
        this.playerId = playerId;
    }

    public String getRole() {
        return role;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public String getPlayerId() {
        return playerId;
    }
}