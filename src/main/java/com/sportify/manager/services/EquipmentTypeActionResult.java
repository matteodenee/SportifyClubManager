package com.sportify.manager.services;

public class EquipmentTypeActionResult {
    private final EquipmentTypeActionStatus status;
    private final String message;

    private EquipmentTypeActionResult(EquipmentTypeActionStatus status, String message) {
        this.status = status;
        this.message = message != null ? message : "";
    }

    public static EquipmentTypeActionResult success(String message) {
        return new EquipmentTypeActionResult(EquipmentTypeActionStatus.SUCCESS, message);
    }

    public static EquipmentTypeActionResult invalid(String message) {
        return new EquipmentTypeActionResult(EquipmentTypeActionStatus.INVALID_INPUT, message);
    }

    public static EquipmentTypeActionResult duplicate(String message) {
        return new EquipmentTypeActionResult(EquipmentTypeActionStatus.DUPLICATE_NAME, message);
    }

    public static EquipmentTypeActionResult inUse(String message) {
        return new EquipmentTypeActionResult(EquipmentTypeActionStatus.IN_USE, message);
    }

    public static EquipmentTypeActionResult error(String message) {
        return new EquipmentTypeActionResult(EquipmentTypeActionStatus.ERROR, message);
    }

    public EquipmentTypeActionStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return status == EquipmentTypeActionStatus.SUCCESS;
    }
}
