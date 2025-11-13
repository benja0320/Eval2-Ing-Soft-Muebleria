package com.example.muebleria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// es una excepción personalizada para indicar que no hay stock suficiente
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String message) {
        super(message);
    }
}