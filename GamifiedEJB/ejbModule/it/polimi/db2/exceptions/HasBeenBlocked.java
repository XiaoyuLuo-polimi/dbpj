package it.polimi.db2.exceptions;

public class HasBeenBlocked extends Exception{
    private static final long serialVersionUID = 1L;

    public HasBeenBlocked(String message) {
        super(message);
    }
}
