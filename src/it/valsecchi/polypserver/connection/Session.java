package it.valsecchi.polypserver.connection;

import it.valsecchi.polypserver.PolypServer;
import it.valsecchi.polypserver.Utility;
import it.valsecchi.polypserver.data.User;
import it.valsecchi.polypserver.exception.GenericException;
import it.valsecchi.polypserver.exception.StreamException;
import it.valsecchi.polypserver.exception.UserAlreadyConnectedException;
import it.valsecchi.polypserver.exception.WrongPasswordException;
import static it.valsecchi.polypserver.Utility.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Rapressenta una sessione di connesione con un User.
 * 
 * @author Davide
 * 
 */
public class Session implements Runnable {

	private Socket socket;
	private User session_user;
	private String session_id;
	private String user_id;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private PolypServer server;
	private boolean session_active = false;

	public Session(Socket socket, PolypServer server) {
		// si memorizza il socket della sessione
		this.server = server;
		this.socket = socket;
		this.session_id = Utility.generateID();
	}

	@Override
	public void run() {
		// si avvia il thread della sessione
		// si acquisiscono gli stream
		this.getStreams();
		// la sessione è attiva
		this.session_active = true;
		// ora si richiedono le informazioni dell'utente da confrontare con il
		// UsersManager
		try {
			this.getUserInfo();
		} catch (StreamException e) {
			Log.error("errore stream");
			this.close();
			return;
		} catch (WrongPasswordException e) {
			this.close();
			return;
		} catch (UserAlreadyConnectedException e) {
			this.close();
			return;
		}
		// ora si richiede la lista file e si invia quella del server
		try {
			this.synchronizeData();
		} catch (StreamException e) {
			Log.error("errore stream");
			this.close();
			return;
		} catch (GenericException e) {
			this.close();
			return;
		}
		// ora si sta in attesa di richieste.
		this.listenForRequests();
	}

	private void listenForRequests() {
		try {
			while (this.session_active) {
				PMessage cmd = this.readMessage();

			}
		} catch (StreamException e) {

		}
	}

	/** Apre gli stream */
	private void getStreams() {
		try {
			input = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
		} catch (IOException e) {
			Log.error("errore apertura streams sessione: " + this.session_id);
		}
	}

	/**
	 * Recupera le informazioni dell'utente e lo registra
	 * 
	 * @throws StreamException
	 * @throws WrongPasswordException
	 * @throws UserAlreadyConnectedException
	 */
	public void getUserInfo() throws StreamException, WrongPasswordException,
			UserAlreadyConnectedException {
		String user_info = "";
		user_info = this.readString();
		StringTokenizer tok = new StringTokenizer(user_info, "@");
		String userid, username, password;
		userid = tok.nextToken();
		username = tok.nextToken();
		password = tok.nextToken();
		// si aggiunge l'utente
		try {
			server.users_manager.addUser(userid, username, password);
		} catch (WrongPasswordException e) {
			// password errata
			Log.error("User: " + userid + ", password errata!");
			// si invia la risposta
			this.sendMessage(PMessage.WRONG_PASSWORD);
			throw e;
		}
		// si attiva l'utente
		boolean connected = server.users_manager.activateUser(userid);
		if (connected == false) {
			// si chiude perchè è già connesso
			Log.error("user già connesso: " + userid);
			this.sendMessage(PMessage.ALREADY_CONNECTED);
			throw new UserAlreadyConnectedException();
		}
		// si indica all'utente l'avvenuta connessione
		this.sendMessage(PMessage.CONNECTED);
		this.user_id = userid;
	}

	/** Si chiude la sessione */
	public void close() {
		try {
			Log.info("chiusura sessione: " + this.session_id);
			output.flush();
			output.close();
			input.close();
			socket.close();
			// si disattiva l'utente
			server.users_manager.deactivateUser(this.user_id);
			// si elimina la sessione
			server.sessions_manager.removeSession(this);
			// la sessione non è più attiva
			session_active = false;
		} catch (IOException e) {
			Log.error("errore chiusura sessione: " + this.session_id);
		}
	}

	/**
	 * Metodo che gestisasce lo scambio della lista file
	 * 
	 * @throws StreamException
	 * @throws GenericException
	 */
	private void synchronizeData() throws StreamException, GenericException {
		List<String> file_list = null;
		try {
			file_list = this.readFileList();
		} catch (StreamException e) {
			// si risponde che c'è stato un'errore
			this.sendMessage(PMessage.ERRORE_LISTA_FILE);
			throw e;
		}
		// si invia l'ok
		this.sendMessage(PMessage.OK);
		// si invia al file manager per aggiornarla
		server.files_manager.setFileList(this.user_id, file_list);
		// ora si invia la lista file completa
		this.sendFileList();
		// si attende la risposta
		PMessage m = this.readMessage();
		if (m == PMessage.OK) {
			// ok si prosegue
		} else if (m == PMessage.ERROR) {
			// Si chiude la connessione
			this.close();
			Log.error("errore comunicazione lista file");
			throw new GenericException();
		}
		// si invia la lista utenti
		this.sendAvaibleUserList();
		// si attende la risposta
		PMessage m2 = this.readMessage();
		if (m2 == PMessage.OK) {
			// ok la connessione è effettuata
			this.sendMessage(PMessage.SYNCHRONIZED);
			//la connessione è effettuata
		} else if (m2 == PMessage.ERROR) {
			// Si chiude la connessione
			this.close();
			Log.error("errore comunicazione lista utenti attivi");
			throw new GenericException();
		}
	}

	/** Metodo che legge i messaggi inviati dal client */
	private PMessage readMessage() throws StreamException {
		try {
			return (PMessage) input.readObject();
		} catch (ClassNotFoundException e) {
			Log.error("errore lettura messaggio");
			throw new StreamException("errore lettura messaggio");
		} catch (IOException e1) {
			Log.error("errore lettura stringa");
			throw new StreamException("errore lettura messaggio");
		}
	}

	/**
	 * Metodo che legge le stringhe inviate dal client
	 * 
	 * @throws StreamException
	 */
	private String readString() throws StreamException {
		try {
			return (String) input.readObject();
		} catch (ClassNotFoundException e) {
			Log.error("errore lettura stringa");
			throw new StreamException("errore lettura stringa");
		} catch (IOException e1) {
			Log.error("errore lettura stringa");
			throw new StreamException("errore lettura stringa");
		}
	}

	private List<String> readFileList() throws StreamException {
		try {
			return (List<String>) input.readObject();
		} catch (ClassNotFoundException e) {
			Log.error("errore lettura lista file");
			throw new StreamException("errore lettura lista file");
		} catch (IOException e1) {
			Log.error("errore lettura lista file");
			throw new StreamException("errore lettura lista file");
		}
	}

	public void sendFileList() throws StreamException {
		try {
			output.writeObject(server.files_manager.getFileList());
		} catch (IOException e) {
			Log.error("errore invio lista file");
			throw new StreamException("errore invio lista file");
		}
	}

	public void sendAvaibleUserList() throws StreamException {
		try {
			output.writeObject(server.users_manager.getListAvaibleUser());
		} catch (IOException e) {
			Log.error("errore invio lista utenti attivi");
			throw new StreamException("errore invio lista file");
		}
	}

	public void sendMessage(PMessage ans) throws StreamException {
		try {
			output.writeObject(ans);
			output.flush();
		} catch (IOException e) {
			Log.error("errore invio messaggio");
			throw new StreamException("errore invio messaggio");
		}
	}
	
	protected byte[] readNBytes(int n) throws StreamException{
		byte[] buf = new byte[n];
		//si leggono
		try {
			input.read(buf, 0, n);
		} catch (IOException e) {
			Log.error("errore lettura trasmissione dati");
			throw new StreamException("errore lettura trasmissione dati");
		}
		return buf;
	}
	
	protected void writeNBytes(byte[] buf) throws StreamException{
		//si scrivono
		try {
			output.write(buf);
			output.flush();
		} catch (IOException e) {
			Log.error("errore scrittura trasmissione dati");
			throw new StreamException("errore scrittura trasmissione dati");
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public User getSession_user() {
		return session_user;
	}

	public String getSession_id() {
		return session_id;
	}
}
