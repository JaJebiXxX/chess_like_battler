package com.akcimabram.chessrbattlr.logic;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.UUID;

public abstract class Figure {

    private final UUID id;
    private int health;

    private int cost;
    private int damage;

    @JsonIgnore
    private List<Field> possibleMoves;
    private String type;
    private String color;

    public Figure(int health, int cost, int damage) {
        this.id = UUID.randomUUID();
        this.health = health;
        this.cost = cost;
        this.damage = damage;
        this.type = "TRIANGLE";
        this.color = "WHITE";
    }

    public UUID getId() {
        return id;
    }

    public abstract void move();

    public abstract void possibleMoves(Board board, Field currentField);

    public abstract void safeDelete();

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void lowerHealth(int howMuch) {
        this.health -= howMuch;
        if (health <= 0) {
            safeDelete();
        }
    }

    public void increaseHealth(int howMuch) {
        this.health += howMuch;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonIgnore
    public List<Field> getPossibleMoves() {
        return possibleMoves;
    }

    @JsonIgnore
    public void setPossibleMoves(List<Field> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }
}
