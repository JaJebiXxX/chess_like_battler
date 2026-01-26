package com.akcimabram.chessrbattlr.site.controller;

import com.akcimabram.chessrbattlr.logic.Figure;
import com.akcimabram.chessrbattlr.logic.GameState;
import com.akcimabram.chessrbattlr.logic.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/figures")
    public ResponseEntity<List<Figure>> getAvailableFigures() {
        return ResponseEntity.ok(gameService.getAvailableFigures());
    }

    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState() {
        return ResponseEntity.ok(gameService.getGameState());
    }

    @PostMapping("/move/{fromX}/{fromY}/{toX}/{toY}")
    public ResponseEntity<GameState> moveFigure(
            @PathVariable int fromX,
            @PathVariable int fromY,
            @PathVariable int toX,
            @PathVariable int toY) {
        gameService.moveFigure(fromX, fromY, toX, toY);
        return ResponseEntity.ok(gameService.getGameState());
    }

    @PostMapping("/place/{type}/{color}/{x}/{y}")
    public ResponseEntity<GameState> placeFigure(
            @PathVariable String type,
            @PathVariable String color,
            @PathVariable int x,
            @PathVariable int y) {
        gameService.placeFigure(type, color, x, y);
        return ResponseEntity.ok(gameService.getGameState());
    }
}
