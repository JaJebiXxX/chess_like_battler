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

        // Directions: dx, dy and max distance
        int[][] directions = {
                {0, 1, 2},   // Up
                {0, -1, 2},  // Down
                {1, 0, 2},   // Right
                {-1, 0, 2},  // Left
                {1, 1, 1},   // Diagonals max 1
                {1, -1, 1},
                {-1, 1, 1},
                {-1, -1, 1}
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int maxDist = dir[2];

            for (int j = 1; j <= maxDist; j++) {
                int nextX = x + dx * j;
                int nextY = y + dy * j;

                if (nextX >= 0 && nextX < board.getX() && nextY >= 0 && nextY < board.getY()) {
                    Field targetField = board.fields[nextX][nextY];
                    if (targetField.whosHere == null) {
                        moves.add(targetField);
                    } else {
                        if (!targetField.whosHere.getColor().equals(myColor)) {
                            moves.add(targetField);
                        }
                        break; // Blocked
                    }
                } else {
                    break; // Out of bounds
                }
            }
        }
        this.setPossibleMoves(moves);
    }

    @Override
    public void safeDelete() {

    }

}
