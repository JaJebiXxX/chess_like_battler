package com.akcimabram.chessrbattlr.logic;

import com.akcimabram.chessrbattlr.logic.Figures.*;
import com.akcimabram.chessrbattlr.logic.enums.FieldType;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    private Board board;

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
        
        // Add a test figure
    }

    public Board getBoard() {
        return board;
    }

    public void moveFigure(int fromX, int fromY, int toX, int toY) {
        Field fromField = board.fields[fromX][fromY];
        Field toField = board.fields[toX][toY];
        
        if (fromField.whosHere != null) {
            Figure figure = fromField.whosHere;
            // Basic validation - check if toField is in possibleMoves
            if (figure.getPossibleMoves() != null && figure.getPossibleMoves().stream().anyMatch(f -> f.coordinateX == toX && f.coordinateY == toY)) {
                toField.whosHere = figure;
                fromField.whosHere = null;
                // Update possible moves for the new position
                figure.possibleMoves(board, toField);
            }
        }
    }

    public void placeFigure(String type, String color, int x, int y) {
        if (x >= 0 && x < board.getX() && y >= 0 && y < board.getY()) {
            // Validation of placement zones
            if (color.equals("WHITE")) {
                if (y < 7 || y > 9) return;
            } else if (color.equals("BLACK")) {
                if (y < 0 || y > 2) return;
            }

            Figure figure;
            // Map frontend types to backend classes
            switch (type) {
                case "TRIANGLE":
                    figure = new Pawn1();
                    break;
                case "CIRCLE":
                    figure = new Pawn2();
                    break;
                case "HEXAGON":
                    figure = new Pawn3();
                    break;
                case "SQUARE":
                    figure = new Soldier1();
                    break;
                case "STAR":
                    figure = new Soldier2();
                    break;
                case "PLUS":
                    figure = new Soldier3();
                    break;
                default:
                    figure = new Pawn1();
            }
            figure.setType(type);
            figure.setColor(color);
            board.fields[x][y].whosHere = figure;
            figure.possibleMoves(board, board.fields[x][y]);
        }
    }
}
