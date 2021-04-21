package it.polimi.db2.exceptions;

public class DataNotExist extends Exception{
    private static final long serialVersionUID = 1L;

    public DataNotExist(String message) {
        super(message);
    }
}
