package com.akcimabram.chessrbattlr.site.controller;

import com.akcimabram.chessrbattlr.logic.Board;
import com.akcimabram.chessrbattlr.logic.Field;
import com.akcimabram.chessrbattlr.logic.enums.FieldType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/game")
public class GameController {

    @GetMapping("/board")
    public ResponseEntity<Board> getBoard() {

        int value = 0;
        Board board = new Board(10, 10);

        for (int i = 0; i < board.getX(); i++) {
            for (int j = 0; j < board.getY(); j++) {
                FieldType type = FieldType.NORMAL;
                if ((i == 4 || i == 5) && (j == 0 || j == 9)) {
                    type = FieldType.HP_BASE;
                    value = 10;
                }
                board.fields[i][j] = new Field(i, j, type, null, value);
                value = 0;
            }
        }

        return ResponseEntity.ok(board);
    }
}
