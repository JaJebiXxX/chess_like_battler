package com.akcimabram.chessrbattlr.site.controller;

import com.akcimabram.chessrbattlr.logic.exceptions.InvalidMoveException;
import com.akcimabram.chessrbattlr.logic.exceptions.InvalidPlacementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ InvalidMoveException.class, InvalidPlacementException.class })
    public ResponseEntity<Object> handleInvalidGameAction(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }
}
