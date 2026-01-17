package com.akcimabram.chessrbattlr.logic.Figures;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.Figure;

import java.util.ArrayList;

public class Pawn2 extends Figure {

    public Pawn2() {
        super(100, 100, 100);
    }

    @Override
    public void move() {

    }

    @Override
    public void possibleMoves(Board board, Field currentField) {
        int x = currentField.coordinateX;
        int y = currentField.coordinateY;
        ArrayList<Field> moves = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int nextX = x + i;
                int nextY = y + j;

                if (nextX >= 0 && nextX < board.getX() && nextY >= 0 && nextY < board.getY()) {
                    moves.add(board.fields[nextX][nextY]);
                }
            }
        }
        this.setPossibleMoves(moves);
    }

    @Override
    public void safeDelete() {

    }

}
