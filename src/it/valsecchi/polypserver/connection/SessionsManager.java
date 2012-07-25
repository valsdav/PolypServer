package it.valsecchi.polypserver.connection;

import it.valsecchi.polypserver.PolypServer;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Gestore delle sessioni
 * 
 * @author Davide
 * 
 */
public class SessionsManager {
	private List<Session> sessionsList;
	private PolypServer server;

	public SessionsManager(PolypServer server) {
		this.server = server;
		sessionsList = new ArrayList<>();
	}

	/** Metodo che aggiunge alla lista una sessione e la avvia */
	public void addSession(Session session) {
		sessionsList.add(session);
		// si avvia la sessione
		Thread start_session = new Thread(session, session.getSession_id());
		start_session.start();
	}

	public void addSession(Socket socket) {
		// si crea la sessione e la si avvia
		Session session = new Session(socket,server);
		this.addSession(session);
	}
	
	public void removeSession(Session session){
		sessionsList.remove(session);
	}
}
