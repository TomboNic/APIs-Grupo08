package com.uade.tpo.exceptions;

public class DeviceModelDuplicateException extends RuntimeException {
    public DeviceModelDuplicateException() {
        super("Ya existe un modelo con ese nombre en esta marca");
    }
}
