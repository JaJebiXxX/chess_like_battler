package com.akcimabram.chessrbattlr.logic.Figures;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.Figure;

import java.util.ArrayList;

public class Soldier1 extends Figure {

    public Soldier1() {
        super(40, 5, 4);
    }

    @Override
    public void move() {

    }

    @Override
    public void possibleMoves(Board board, Field currentField) {
        int x = currentField.coordinateX;
        int y = currentField.coordinateY;
        ArrayList<Field> moves = new ArrayList<>();

        int[] dx = {-3, -2, -1, 0, 0, 0, 0, 0, 0, 1, 2, 3};
        int[] dy = {0, 0, 0, -3, -2, -1, 1, 2, 3, 0, 0, 0};

        for (int i = 0; i < dx.length; i++) {
            int nextX = x + dx[i];
            int nextY = y + dy[i];

            if (nextX >= 0 && nextX < board.getX() && nextY >= 0 && nextY < board.getY()) {
                moves.add(board.fields[nextX][nextY]);
            }
        }
        this.setPossibleMoves(moves);
    }

    @Override
    public void safeDelete() {

    }

}
