package it.polimi.db2.exceptions;

public class DuplicateInsertion extends Exception{
    private static final long serialVersionUID = 1L;

    public DuplicateInsertion(String message) {
        super(message);
    }
}
