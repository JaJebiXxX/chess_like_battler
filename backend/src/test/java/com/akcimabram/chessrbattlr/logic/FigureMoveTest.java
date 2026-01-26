package com.akcimabram.chessrbattlr.logic;

import com.akcimabram.chessrbattlr.logic.Figures.Pawn1;
import com.akcimabram.chessrbattlr.logic.Figures.Pawn2;
import com.akcimabram.chessrbattlr.logic.enums.FieldType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class FigureMoveTest {

    @Test
    public void testPawn2Moves() {
        Board board = new Board(10, 10);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board.fields[i][j] = new Field(i, j, FieldType.NORMAL, null, 0);
            }
        }
        
        Pawn2 pawn = new Pawn2();
        Field currentField = board.fields[5][5];
        pawn.possibleMoves(board, currentField);
        
        List<Field> moves = pawn.getPossibleMoves();
        assertNotNull(moves);
        assertEquals(8, moves.size(), "Pawn should have 8 possible moves at (5,5)");
    }

    @Test
    public void testPawn1Moves() {
        Board board = new Board(10, 10);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board.fields[i][j] = new Field(i, j, FieldType.NORMAL, null, 0);
            }
        }
        
        Pawn1 knight = new Pawn1();
        Field currentField = board.fields[5][5];
        knight.possibleMoves(board, currentField);
        
        List<Field> moves = knight.getPossibleMoves();
        assertNotNull(moves);
        // Distance 2 in 8 directions + Distance 1 in 8 directions
        assertEquals(16, moves.size(), "Knight should have 16 possible moves at (5,5)");
    }
}
