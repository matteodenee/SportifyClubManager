package com.sportify.manager.services;

/**
 * Représente l'affectation d'un joueur à un rôle spécifique dans une composition.
 */
public class RoleAssignment {
    private final String role;     // ex: "Gardien", "Ailier"
    private final int slotIndex;   // 1..k si le rôle est répété (ex: deux "Attaquants")
    private final String playerId; // Correspond à l'ID de l'utilisateur (User.id)

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