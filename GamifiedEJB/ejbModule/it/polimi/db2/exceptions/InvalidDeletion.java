package it.polimi.db2.exceptions;

public class InvalidDeletion extends Exception{
    private static final long serialVersionUID = 1L;

    public InvalidDeletion(String message) {
        super(message);
    }
}
