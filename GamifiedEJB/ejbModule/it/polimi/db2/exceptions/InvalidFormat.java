package it.polimi.db2.exceptions;

public class InvalidFormat extends Exception{
    private static final long serialVersionUID = 1L;

    public InvalidFormat(String message) {
        super(message);
    }
}
