package com.s2.Logitrack.exception;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String message) {
        super(message);
    }

    public ProductoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado");
    }
}
