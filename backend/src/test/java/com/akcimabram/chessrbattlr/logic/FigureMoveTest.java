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

    @Test
    public void testCombatScenarios() {
        GameService gameService = new GameService();
        gameService.init();
        Board board = gameService.getBoard();

        // --- Scenario 1: Attacker wins (Capture) ---
        Figure attacker1 = new Pawn1(); // Damage: 5, Health: 10
        attacker1.setDamage(10);
        attacker1.setColor("WHITE");
        board.fields[5][5].whosHere = attacker1;
        attacker1.possibleMoves(board, board.fields[5][5]);

        Figure defender1 = new Pawn1(); // Health: 10
        defender1.setHealth(5);
        defender1.setColor("BLACK");
        board.fields[5][6].whosHere = defender1;

        gameService.moveFigure(5, 5, 5, 6);

        assertNull(board.fields[5][5].whosHere, "Attacker should have moved from original field");
        assertSame(attacker1, board.fields[5][6].whosHere, "Attacker should be on the defender's field");


        // --- Scenario 2: Attacker damages but doesn't kill (Stand-off) ---
        gameService.init(); // Reset board
        board = gameService.getBoard();

        Figure attacker2 = new Pawn1(); // Damage: 5
        attacker2.setDamage(5);
        attacker2.setColor("WHITE");
        board.fields[3][3].whosHere = attacker2;
        attacker2.possibleMoves(board, board.fields[3][3]);

        Figure defender2 = new Pawn1(); // Health: 10
        defender2.setHealth(10);
        defender2.setColor("BLACK");
        board.fields[3][5].whosHere = defender2;

        gameService.moveFigure(3, 3, 3, 5);

        assertNull(board.fields[3][3].whosHere, "Attacker should have moved from original field");
        assertSame(defender2, board.fields[3][5].whosHere, "Defender should still be on its field");
        assertEquals(5, defender2.getHealth(), "Defender health should be reduced");
        assertSame(attacker2, board.fields[3][4].whosHere, "Attacker should be on the stand-off field");

    }
}
