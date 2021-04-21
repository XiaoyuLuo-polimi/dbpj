package it.polimi.db2.exceptions;

public class InvalidInsert extends Exception{
    private static final long serialVersionUID = 1L;

    public InvalidInsert(String message) {
        super(message);
    }
}
