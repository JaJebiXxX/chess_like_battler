package com.akcimabram.chessrbattlr.logic.Figures;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.Figure;

import java.util.ArrayList;

public class Pawn3 extends Figure {

    public Pawn3() {
        super(1, 3, 6);
    }

    @Override
    public void move() {

    }

    @Override
    public void possibleMoves(Board board, Field currentField) {
        int x = currentField.coordinateX;
        int y = currentField.coordinateY;
        ArrayList<Field> moves = new ArrayList<>();
        String myColor = this.getColor();

        int[] dx = {-3, -2, -2, -1, -1, 0, 0, 0, 0, 1, 1, 2, 2, 3};
        int[] dy = {0, 0, -2, 0, -1, 2, 1, -1, -2, 0, -1, 0, -2, 0};

        boolean isBlack = "BLACK".equals(this.getColor());

        for (int i = 0; i < dx.length; i++) {
            int currentDy = isBlack ? -dy[i] : dy[i];
            int nextX = x + dx[i];
            int nextY = y + currentDy;

            if (nextX >= 0 && nextX < board.getX() && nextY >= 0 && nextY < board.getY()) {
                Field targetField = board.fields[nextX][nextY];
                if (targetField.whosHere == null || !targetField.whosHere.getColor().equals(myColor)) {
                    moves.add(targetField);
                }
            }
        }
        this.setPossibleMoves(moves);
    }

    @Override
    public void safeDelete() {

    }

}
