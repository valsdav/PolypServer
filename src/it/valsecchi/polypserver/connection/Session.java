package it.valsecchi.polypserver.connection;

import it.valsecchi.polypserver.PolypServer;
import it.valsecchi.polypserver.Utility;
import it.valsecchi.polypserver.data.User;
import it.valsecchi.polypserver.exception.WrongPasswordException;
import static it.valsecchi.polypserver.Utility.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private PolypServer server;

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
		// ora si richiedono le informazioni dell'utente da confrontare con il
		// UsersManager
		this.getUserInfo();

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

	/** Recupera le informazioni dell'utente e lo registra */
	public void getUserInfo() {
		String user_info = "";
		try {
			user_info = (String) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			Log.error("errore ricezione informazioni utente. sessione: "
					+ this.session_id);
		}
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
			// si chiude
			this.close();
		}
		// si attiva l'utente
		server.users_manager.activateUser(userid);

	}

	public void close() {
		try {
			Log.info("chiusura sessione: " + this.session_id);
			output.flush();
			output.close();
			input.close();
			socket.close();
		} catch (IOException e) {
			Log.error("errore chiusura sessione: " + this.session_id);
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
