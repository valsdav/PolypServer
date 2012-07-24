package it.valsecchi.polypserver.exception;

public class WrongPasswordException extends Exception {

	private static final long serialVersionUID = -553673580599059649L;
	private String user_id;

	public WrongPasswordException(String user_id) {
		super("Password sbagliata per utente: " + user_id);
		this.user_id = user_id;
	}

	public String getUserID() {
		return user_id;
	}

}
