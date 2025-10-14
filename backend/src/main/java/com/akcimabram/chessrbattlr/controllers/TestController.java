package com.akcimabram.chessrbattlr.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Allow React frontend
public class TestController {

        @GetMapping("/hello")
        public ResponseEntity<String> sayHello() {
            return ResponseEntity.ok("hello from backend");
        }
}
