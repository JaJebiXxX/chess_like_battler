package com.akcimabram.chessrbattlr.logic;

public class Board {
    public Field[][] fields;
    private final int x;
    private final int y;

    public Board(int x, int y) {
        this.fields = new Field[x][y];
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
