package com.akcimabram.chessrbattlr.logic;

public class Player {
    private String name;
    private int mana;
    private int maxMana;
    private int hp;
    private int maxHp;

    public Player(String name, int mana, int maxMana, int hp, int maxHp) {
        this.name = name;
        this.mana = mana;
        this.maxMana = maxMana;
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
}
