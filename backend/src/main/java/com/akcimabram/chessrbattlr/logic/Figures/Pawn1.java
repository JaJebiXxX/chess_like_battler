package com.akcimabram.chessrbattlr.logic.Figures;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.Figure;

import java.util.ArrayList;

public class Pawn1 extends Figure {

    public Pawn1() {
        super(25, 3, 5);
    }

    @Override
    public void move() {

    }

    @Override
    public void possibleMoves(Board board, Field currentField) {
        int x = currentField.coordinateX;
        int y = currentField.coordinateY;
        ArrayList<Field> moves = new ArrayList<>();

        int[] dx = {-2, 0, 2, -1, -1, -1, 0, 0, 1, 1, 1, 0};
        int[] dy = {0, -2, 0, -1, 0, 1, -1, 1, -1, 0, 1, 2};

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
