package com.akcimabram.chessrbattlr.logic.Figures;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.Figure;

import java.util.ArrayList;

public class Soldier3 extends Figure {

    public Soldier3() {
        super(50, 0, 1);
    }

    @Override
    public void move() {

    }

    @Override
    public void possibleMoves(Board board, Field currentField) {
        int x = currentField.coordinateX;
        int y = currentField.coordinateY;
        ArrayList<Field> moves = new ArrayList<>();

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

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
