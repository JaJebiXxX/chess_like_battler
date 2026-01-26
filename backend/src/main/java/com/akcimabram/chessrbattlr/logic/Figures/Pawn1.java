package com.akcimabram.chessrbattlr.logic.Figures;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.Figure;

import java.util.ArrayList;

public class Pawn1 extends Figure {

    public Pawn1() {
        super(6, 3, 5);
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

        int[] dx = {0, 0, 1, -1, 1, 1, -1, -1}; // 8 directions
        int[] dy = {1, -1, 0, 0, 1, -1, 1, -1};

        for (int i = 0; i < 8; i++) {
            for (int j = 1; j <= 2; j++) { // Max move distance is 2
                int nextX = x + dx[i] * j;
                int nextY = y + dy[i] * j;

                if (nextX >= 0 && nextX < board.getX() && nextY >= 0 && nextY < board.getY()) {
                    Field targetField = board.fields[nextX][nextY];
                    if (targetField.whosHere == null) {
                        // Empty square, can move here
                        moves.add(targetField);
                    } else {
                        // Square is occupied
                        if (!targetField.whosHere.getColor().equals(myColor)) {
                            // It's an enemy, can capture
                            moves.add(targetField);
                        }
                        // Path is blocked, stop searching in this direction
                        break;
                    }
                } else {
                    // Out of bounds, stop searching in this direction
                    break;
                }
            }
        }
        this.setPossibleMoves(moves);
    }

    @Override
    public void safeDelete() {

    }

}
