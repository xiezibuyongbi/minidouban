package com.minidouban.cachemgr.exception;

public class UnrecognizedSqlTypeException extends Exception {
    private static final String DESCRIPTION = "unrecognized sql cause type from mq message ";

    public UnrecognizedSqlTypeException() {
        this("");
    }

    public UnrecognizedSqlTypeException(String message) {
        super(DESCRIPTION + message);
    }

}
