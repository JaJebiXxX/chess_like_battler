package com.akcimabram.chessrbattlr.logic;

public abstract class Figure {

    private int health;

    private int cost;

    private int damage;

    public Figure(int health, int cost, int damage) {
        this.health = health;
        this.cost = cost;
        this.damage = damage;
    }

    public abstract void move();

    public abstract void possibleMoves();

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
}
