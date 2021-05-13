package it.polimi.db2.exceptions;

public class OffensiveWordsInsert extends Exception{
    private static final long serialVersionUID = 1L;

    public OffensiveWordsInsert(String message) {
        super(message);
    }
}
