package com.minidouban.cachemgr.exception;

public class UnrecognizedTableException extends Exception {
    private static final String DESCRIPTION = "unrecognized table name from mq message ";

    public UnrecognizedTableException() {
        this("");
    }

    public UnrecognizedTableException(String message) {
        super(DESCRIPTION + message);
    }
}
