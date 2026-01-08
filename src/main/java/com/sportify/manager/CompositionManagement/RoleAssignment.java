package com.sportify.manager.CompositionManagement;

public class RoleAssignment {
    private final String role;     // ex: "Gardien", "Ailier"
    private final int slotIndex;   // 1..k si role répété
    private final String playerId; // User.id

    public RoleAssignment(String role, int slotIndex, String playerId) {
        this.role = role;
        this.slotIndex = slotIndex;
        this.playerId = playerId;
    }

    public String getRole() { return role; }
    public int getSlotIndex() { return slotIndex; }
    public String getPlayerId() { return playerId; }
}
