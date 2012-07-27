package it.valsecchi.polypserver.connection;

import it.valsecchi.polypserver.PolypServer;
import it.valsecchi.polypserver.exception.StreamException;

import java.net.Socket;
import java.util.ArrayList;
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
		sessionsList = new ArrayList<Session>();
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
	
	public PMessage performFileRequest(String filename, Session richiedente) throws StreamException{
		//si trova l'id dell'user a cui bisogna chiedere il file
		String userid = server.files_manager.getUserIDFromFile(filename);
		//si cerca la sessione giusta
		Session fonte= null;
		for(Session s:sessionsList){
			if(s.getUser_id().equals(userid)){
				fonte = s;
			}
		}
		if(fonte==null){
			return PMessage.FILE_NOT_AVAIABLE;
		}else{
			//si invia la richiesta alla sessione se non è busy
			if(fonte.isSessionBusy()==true){
				return PMessage.USER_BUSY;
			}else{
				//si invia la richiesta
				fonte.busy();
				fonte.sendMessage(PMessage.FILE_REQUEST);
				//si invia il nomefile
				fonte.sendString(filename);
				//ora si attende una risposta 
				PMessage result =  fonte.readMessage();
				if(result == PMessage.OK){
					//Si avvia la trasmissione
				}
			}
		}
	}
	
	private PMessage performFileTransfer(String filename,Session richiedente, Session fonte){
		
	}
}
