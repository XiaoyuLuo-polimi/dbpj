package it.polimi.db2.exceptions;

public class CannotConnectToDB extends Exception {
	private static final long serialVersionUID = 1L;

	public CannotConnectToDB(String message) {
		super(message);
	}
}
