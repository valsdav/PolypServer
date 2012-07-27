package it.valsecchi.polypserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import it.valsecchi.polypserver.connection.SessionsManager;
import it.valsecchi.polypserver.data.FilesManager;
import it.valsecchi.polypserver.data.UsersManager;
import static it.valsecchi.polypserver.Utility.Log;

/**
 * Oggetto server.
 * 
 * @author Davide
 * 
 */
public class PolypServer implements Runnable {

	public UsersManager users_manager;
	public SessionsManager sessions_manager;
	public FilesManager files_manager;
	private ServerSocket polyp_server;
	private String polyp_name;
	private int port;
	private int max_clients;
	public String polyp_password;
	public String polyp_path;
	/** Variabile che controlla la continuazione dell'esecuzione del server */
	private boolean run_polyp_server;
	private Thread polyp_thread;

	public PolypServer(String polyp_name, int port, int max_clients,
			String password, String path) {
		users_manager = new UsersManager(this);
		sessions_manager = new SessionsManager(this);
		files_manager = new FilesManager(this);
		this.polyp_name = polyp_name;
		this.port = port;
		this.max_clients = max_clients;
		this.polyp_password = password;
		this.polyp_path = path;
		// inizializzo il server
		try {
			polyp_server = new ServerSocket(this.port, this.max_clients);
		} catch (IOException e) {
			Log.error("errore creazione polyp_server: " + this.polyp_name);
		}
	}

	/**
	 * Metodo che avvia il server
	 * 
	 * @throws Exception
	 */
	public void startPolyp() throws Exception {
		// inizializzo il server
		try {
			polyp_server = new ServerSocket(this.port, this.max_clients);
		} catch (IOException e) {
			Log.error("errore creazione polyp_server: " + this.polyp_name);
			this.run_polyp_server = false;
			throw new Exception("errore creazione polyp_server: "
					+ this.polyp_name);
		}
		this.run_polyp_server = true;
		// si avvia su thread separato la ricezione client del server
		this.polyp_thread = new Thread(this,
				"Thread principale del PolypServer");
		this.polyp_thread.start();
		// si prosegue consentendo l'interazione con l'utente.
		this.startServerShell();		
	}

	/**
	 * Metodo che ferma il server
	 */
	public void stopPolyp() {
		// si salvano tutti i dati
		this.users_manager.writeUsers();
		this.files_manager.writeFiles();
		// si ferma il ciclo
		this.run_polyp_server = false;
		//TODO controllare chiusura server
	}

	@Override
	/**
	 * Metodo che avvia il processo base del server per ricevere richieste client su un thread separato.
	 */
	public void run() {
		// attesa connessioni
		while (this.run_polyp_server) {
			Log.info("server in attesa di connessioni...");
			try {
				Socket socket = polyp_server.accept();
				Log.info("connessione stabilita con: "
						+ socket.getInetAddress());
				//si esce se non si può continuare a utilizzare il server
				if (this.run_polyp_server == false) {
					socket.close();
					return;
				}
				// si crea e aggiunge la sessione
				sessions_manager.addSession(socket);
			} catch (IOException e) {
				Log.error("errore nella connessione al server...");
			}
		}
	}
	
	private void startServerShell(){
		
	}

}
