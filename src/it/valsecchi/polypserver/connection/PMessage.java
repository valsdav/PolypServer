package it.valsecchi.polypserver.connection;

/**
 * Enumeratore che rappresenta i messaggi che si scambiano PolypServer e Client;
 * @author Davide Valsecchi
 *
 */
public enum PMessage {

	/** Comunicazione andata a buon fine*/
	OK,
	/** Errore generico*/
	ERROR,
	/** Errore trasmissione lista file*/
	ERRORE_LISTA_FILE,
	/** Password scorretta*/
	WRONG_PASSWORD,
	/** Utente già connesso*/
	ALREADY_CONNECTED,
    /** Utente correttamente connesso*/
	CONNECTED,
	/** Utente sincronizzato*/
	SYNCHRONIZED;
	
	
			



}
