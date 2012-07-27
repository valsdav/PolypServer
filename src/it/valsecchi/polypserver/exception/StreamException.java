package it.valsecchi.polypserver.exception;

import it.valsecchi.polypserver.connection.PMessage;

public class StreamException extends Exception {

	private static final long serialVersionUID = 121931363756498398L;

	public StreamException(String mess) {
		super(mess);
	}
}
