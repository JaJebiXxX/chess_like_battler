package com.akcimabram.chessrbattlr.logic;

import com.akcimabram.chessrbattlr.logic.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class Field {
    public int coordinateX;
    public int coordinateY;
    public FieldType fieldType;
    public Figure whosHere;
    @JsonIgnore
    public int value;

    private List<FieldCoords> possibleMoves;

    public static class FieldCoords {
        public int x;
        public int y;
        public FieldCoords(int x, int y) { this.x = x; this.y = y; }
    }

    public List<FieldCoords> getPossibleMoves() {
        if (whosHere == null || whosHere.getPossibleMoves() == null) return null;
        return whosHere.getPossibleMoves().stream()
                .map(f -> new FieldCoords(f.coordinateX, f.coordinateY))
                .toList();
    }

    public Field(int coordinateX, int coordinateY, FieldType fieldType, Figure whosHere, int value) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.fieldType = fieldType;
        this.whosHere = whosHere;
        this.value = value;
    }
}
