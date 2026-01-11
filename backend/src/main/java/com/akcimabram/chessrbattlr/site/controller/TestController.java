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
@RequestMapping("/xd")
public class TestController {

    @GetMapping("/{nazwa}")
    public ResponseEntity<Board> test(@PathVariable String nazwa) {

        Board board = new Board(10, 10);
        for (int i = 0; i < board.getX(); i++) {
            for (int j = 0; j < board.getY(); j++) {
                board.fields[i][j] = new Field(i, j, FieldType.NORMAL, null);
            }
        }

        return ResponseEntity.ok(board);
    }
}
