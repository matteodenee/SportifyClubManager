package com.sportify.manager.services;

public class Equipment {
    private int id;
    private String name;
    private String type;
    private String condition;
    private int quantity;

    public Equipment(int id, String name, String type, String condition, int quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.quantity = quantity;
    }

    public Equipment(String name, String type, String condition, int quantity) {
        this(0, name, type, condition, quantity);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getCondition() { return condition; }
    public int getQuantity() { return quantity; }

    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
