package com.akcimabram.chessrbattlr.logic;

import com.akcimabram.chessrbattlr.logic.enums.FieldType;

public class Field {
    public int coordinateX;
    public int coordinateY;
    public FieldType fieldType;
    public Figure whosHere;
    public int value;

    public Field(int coordinateX, int coordinateY, FieldType fieldType, Figure whosHere, int value) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.fieldType = fieldType;
        this.whosHere = whosHere;
        this.value = value;
    }
}


//board 10x10 fieldami
