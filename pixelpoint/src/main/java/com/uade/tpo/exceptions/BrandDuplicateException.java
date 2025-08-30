package com.uade.tpo.exceptions;

public class BrandDuplicateException extends RuntimeException {
    public BrandDuplicateException() {
        super("La marca ya existe");
    }
}
