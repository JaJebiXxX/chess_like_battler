package com.akcimabram.chessrbattlr.logic;

import com.akcimabram.chessrbattlr.logic.Figures.*;
import com.akcimabram.chessrbattlr.logic.enums.FieldType;
import com.akcimabram.chessrbattlr.logic.exceptions.GameException;
import com.akcimabram.chessrbattlr.logic.exceptions.InvalidMoveException;
import com.akcimabram.chessrbattlr.logic.exceptions.InvalidPlacementException;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class GameService {
    private Board board;
    private Player player1;
    private Player player2;
    private UUID currentPlayerId;
    private List<UUID> movedFigureIds;
    private int turn;
    private boolean specialTileActivated;

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
        player1 = new Player(UUID.randomUUID(), "Player 1", 1, 10, 10, 10);
        player2 = new Player(UUID.randomUUID(), "Player 2", 1, 10, 10, 10);
        currentPlayerId = player1.getId();
        movedFigureIds = new ArrayList<>();
        turn = 1;
        specialTileActivated = false;

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

        spawnSpecialTile();

        recalculateAllPossibleMoves();
    }

    private void spawnSpecialTile() {
        // Random special tile from y3 to y6 and x0 to x9
        Random rand = new Random();
        int specialX = rand.nextInt(10);
        int specialY = rand.nextInt(4) + 3; // y3 to y6

        FieldType[] specialTypes = {FieldType.MANA, FieldType.HP_FIGURE, FieldType.HP_BASE, FieldType.ATT_FIGURE};
        FieldType randomType = specialTypes[rand.nextInt(specialTypes.length)];
        board.fields[specialX][specialY].fieldType = randomType;
    }

    public GameState getGameState() {
        return new GameState(board, player1, player2, currentPlayerId, movedFigureIds, turn);
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

        // Turn validation
        Player currentPlayer = getCurrentPlayer();
        if (!isPlayerTurn(attacker.getColor(), currentPlayer)) {
            throw new GameException("It's not your turn.");
        }
        if (movedFigureIds.contains(attacker.getId())) {
            throw new InvalidMoveException("This figure has already moved this turn.");
        }

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
        }
        else {
            // Simple move
            toField.whosHere = attacker;
            fromField.whosHere = null;
        }
        movedFigureIds.add(attacker.getId());
        applySpecialFieldEffects(toField, attacker);

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

    private void applySpecialFieldEffects(Field field, Figure figure) {
        if (field.fieldType == null || field.fieldType == FieldType.NORMAL) {
            return;
        }

        Player currentPlayer = getCurrentPlayer();
        specialTileActivated = true;

        switch (field.fieldType) {
            case MANA:
                currentPlayer.setMana(currentPlayer.getMana() + 1);
                break;
            case HP_FIGURE:
                figure.setHealth(figure.getHealth() + 1);
                break;
            case HP_BASE:
                // Check if it's NOT a base tile (the ones at y=0 or y=9)
                if (field.coordinateY != 0 && field.coordinateY != 9) {
                    currentPlayer.setHp(currentPlayer.getHp() + 1);
                }
                break;
            case ATT_FIGURE:
                figure.setDamage(figure.getDamage() + 1);
                break;
        }
    }

    public void placeFigure(String type, String color, int x, int y) {
        // Turn validation
        Player currentPlayer = getCurrentPlayer();
        if (!isPlayerTurn(color, currentPlayer)) {
            throw new GameException("It's not your turn.");
        }

        if (x < 0 || x >= board.getX() || y < 0 || y >= board.getY()) {
            throw new InvalidPlacementException("Cannot place figure outside the board.");
        }

        if (color.equals("WHITE") && (y < 7 || y > 9)) {
            throw new InvalidPlacementException("White figures can only be placed in the bottom three rows.");
        } else if (color.equals("BLACK") && (y < 0 || y > 2)) {
            throw new InvalidPlacementException("Black figures can only be placed in the top three rows.");
        }

        Field targetField = board.fields[x][y];
        if (targetField.whosHere != null) {
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

        if (currentPlayer.getMana() < figure.getCost()) {
            throw new InvalidPlacementException("Not enough mana.");
        }
        currentPlayer.setMana(currentPlayer.getMana() - figure.getCost());

        figure.setType(type);
        figure.setColor(color);
        targetField.whosHere = figure;
        movedFigureIds.add(figure.getId());

        applySpecialFieldEffects(targetField, figure);

        recalculateAllPossibleMoves();
    }

    public void endTurn() {
        if (currentPlayerId.equals(player1.getId())) {
            currentPlayerId = player2.getId();
            player2.setMana(Math.min(player2.getMaxMana(), turn));
        } else {
            currentPlayerId = player1.getId();
            turn++;
            player1.setMana(Math.min(player1.getMaxMana(), turn));
        }

        if (specialTileActivated) {
            rotateSpecialTile();
            specialTileActivated = false;
        }

        movedFigureIds.clear();
    }

    private void rotateSpecialTile() {
        // Find current special tile and make it normal
        for (int i = 0; i < board.getX(); i++) {
            for (int j = 0; j < board.getY(); j++) {
                FieldType type = board.fields[i][j].fieldType;
                // Normalize all special tiles, but keep the permanent base tiles at the edges
                if (type != FieldType.NORMAL) {
                    if (type == FieldType.HP_BASE) {
                        // Only keep HP_BASE if it's on the edge rows (0 or 9)
                        int y = board.fields[i][j].coordinateY;
                        if (y != 0 && y != 9) {
                            board.fields[i][j].fieldType = FieldType.NORMAL;
                        }
                    } else {
                        board.fields[i][j].fieldType = FieldType.NORMAL;
                    }
                }
            }
        }
        // Spawn a new one
        spawnSpecialTile();
    }

    private Player getCurrentPlayer() {
        if (currentPlayerId.equals(player1.getId())) {
            return player1;
        } else {
            return player2;
        }
    }

    private boolean isPlayerTurn(String color, Player currentPlayer) {
        if (color.equals("WHITE") && currentPlayer.getId().equals(player1.getId())) {
            return true;
        }
        return color.equals("BLACK") && currentPlayer.getId().equals(player2.getId());
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
