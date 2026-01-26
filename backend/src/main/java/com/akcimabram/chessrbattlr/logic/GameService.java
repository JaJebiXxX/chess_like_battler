package com.akcimabram.chessrbattlr.logic;

import com.akcimabram.chessrbattlr.logic.Figures.*;
import com.akcimabram.chessrbattlr.logic.enums.FieldType;
import com.akcimabram.chessrbattlr.logic.exceptions.InvalidMoveException;
import com.akcimabram.chessrbattlr.logic.exceptions.InvalidPlacementException;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    private Board board;
    private Player player1;
    private Player player2;

    public List<Figure> getAvailableFigures() {
        List<Figure> figures = new ArrayList<>();
        
        Figure f;
        
        f = new Pawn2(); f.setType("CIRCLE"); f.setColor("WHITE"); figures.add(f);
        f = new Pawn1(); f.setType("TRIANGLE"); f.setColor("WHITE"); figures.add(f);
        f = new Pawn3(); f.setType("HEXAGON"); f.setColor("WHITE"); figures.add(f);
        f = new Soldier1(); f.setType("SQUARE"); f.setColor("WHITE"); figures.add(f);
        f = new Soldier2(); f.setType("STAR"); f.setColor("WHITE"); figures.add(f);
        f = new Soldier3(); f.setType("PLUS"); f.setColor("WHITE"); figures.add(f);

        f = new Pawn2(); f.setType("CIRCLE"); f.setColor("BLACK"); figures.add(f);
        f = new Pawn1(); f.setType("TRIANGLE"); f.setColor("BLACK"); figures.add(f);
        f = new Pawn3(); f.setType("HEXAGON"); f.setColor("BLACK"); figures.add(f);
        f = new Soldier1(); f.setType("SQUARE"); f.setColor("BLACK"); figures.add(f);
        f = new Soldier2(); f.setType("STAR"); f.setColor("BLACK"); figures.add(f);
        f = new Soldier3(); f.setType("PLUS"); f.setColor("BLACK"); figures.add(f);
        
        return figures;
    }

    @PostConstruct
    public void init() {
        board = new Board(10, 10);
        player1 = new Player("Player 1", 3, 10, 10, 10);
        player2 = new Player("Player 2", 3, 10, 10, 10);

        for (int i = 0; i < board.getX(); i++) {
            for (int j = 0; j < board.getY(); j++) {
                FieldType type = FieldType.NORMAL;
                int value = 0;
                if ((i == 4 || i == 5) && (j == 0 || j == 9)) {
                    type = FieldType.HP_BASE;
                    value = 10;
                }
                board.fields[i][j] = new Field(i, j, type, null, value);
            }
        }
        recalculateAllPossibleMoves();
    }

    public GameState getGameState() {
        return new GameState(board, player1, player2);
    }

    private void recalculateAllPossibleMoves() {
        for (int i = 0; i < board.getX(); i++) {
            for (int j = 0; j < board.getY(); j++) {
                Field field = board.fields[i][j];
                if (field.whosHere != null) {
                    field.whosHere.possibleMoves(board, field);
                }
            }
        }
    }

    public void moveFigure(int fromX, int fromY, int toX, int toY) {
        Field fromField = board.fields[fromX][fromY];
        Field toField = board.fields[toX][toY];

        if (fromField.whosHere == null) {
            throw new InvalidMoveException("No figure at the source position.");
        }

        Figure attacker = fromField.whosHere;

        if (attacker.getPossibleMoves() == null || attacker.getPossibleMoves().stream().noneMatch(f -> f.coordinateX == toX && f.coordinateY == toY)) {
            throw new InvalidMoveException("The move is not possible for this figure.");
        }

        Figure defender = toField.whosHere;

        if (defender != null) {
            if (attacker.getColor().equals(defender.getColor())) {
                throw new InvalidMoveException("Cannot attack an allied piece.");
            }
            // Combat logic
            if (attacker.getDamage() >= defender.getHealth()) {
                toField.whosHere = attacker;
                fromField.whosHere = null;
            } else {
                defender.setHealth(defender.getHealth() - attacker.getDamage());
                int dx = toX - fromX;
                int dy = toY - fromY;
                int stepX = Integer.signum(dx);
                int stepY = Integer.signum(dy);
                int standoffX = toX - stepX;
                int standoffY = toY - stepY;

                if (standoffX >= 0 && standoffX < board.getX() && standoffY >= 0 && standoffY < board.getY() && board.fields[standoffX][standoffY].whosHere == null) {
                    board.fields[standoffX][standoffY].whosHere = attacker;
                    fromField.whosHere = null;
                }
            }
        } else {
            // Simple move
            toField.whosHere = attacker;
            fromField.whosHere = null;
        }

        // Check for base attack
        Figure finalFigureOnToField = toField.whosHere;
        if (finalFigureOnToField != null && toField.fieldType == FieldType.HP_BASE) {
            if (finalFigureOnToField.getColor().equals("WHITE") && toField.coordinateY == 0) {
                player2.setHp(player2.getHp() - finalFigureOnToField.getDamage());
                toField.whosHere = null;
            } else if (finalFigureOnToField.getColor().equals("BLACK") && toField.coordinateY == 9) {
                player1.setHp(player1.getHp() - finalFigureOnToField.getDamage());
                toField.whosHere = null;
            }
        }

        recalculateAllPossibleMoves();
    }

    public void placeFigure(String type, String color, int x, int y) {
        if (x < 0 || x >= board.getX() || y < 0 || y >= board.getY()) {
            throw new InvalidPlacementException("Cannot place figure outside the board.");
        }

        if (color.equals("WHITE") && (y < 7 || y > 9)) {
            throw new InvalidPlacementException("White figures can only be placed in the bottom three rows.");
        } else if (color.equals("BLACK") && (y < 0 || y > 2)) {
            throw new InvalidPlacementException("Black figures can only be placed in the top three rows.");
        }

        if (board.fields[x][y].whosHere != null) {
            throw new InvalidPlacementException("Cannot place figure on an occupied field.");
        }

        Figure figure;
        switch (type) {
            case "TRIANGLE": figure = new Pawn1(); break;
            case "CIRCLE": figure = new Pawn2(); break;
            case "HEXAGON": figure = new Pawn3(); break;
            case "SQUARE": figure = new Soldier1(); break;
            case "STAR": figure = new Soldier2(); break;
            case "PLUS": figure = new Soldier3(); break;
            default: throw new InvalidPlacementException("Unknown figure type: " + type);
        }
        figure.setType(type);
        figure.setColor(color);
        board.fields[x][y].whosHere = figure;
        recalculateAllPossibleMoves();
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
