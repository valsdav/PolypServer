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
public class PolypServer {

	public UsersManager users_manager;
	public SessionsManager sessions_manager;
	public FilesManager files_manager;
	private ServerSocket polyp_server;
	private String polyp_name;
	private int port;
	private int max_clients;
	public String polyp_password;
	public String polyp_path;
	private boolean run_polyp_server = true;

	public PolypServer(String polyp_name, int port, int max_clients,String password, String path) {
		users_manager = new UsersManager(this);
		sessions_manager = new SessionsManager(this);
		files_manager = new FilesManager(this);
		this.polyp_name = polyp_name;
		this.port = port;
		this.max_clients = max_clients;
		this.polyp_password= password;
		this.polyp_path= path;
		// inizializzo il server
		try {
			polyp_server = new ServerSocket(this.port, this.max_clients);
		} catch (IOException e) {
			Log.error("errore creazione polyp_server: " + this.polyp_name);
		}
	}

	/**
	 * Metodo che avvia il server
	 */
	public void runPolyp() {
		// attesa connessioni
		while (this.run_polyp_server) {
			Log.info("server in attesa di connessioni...");
			try {
				Socket socket = polyp_server.accept();
				Log.info("connessione stabilita con: " +socket.getInetAddress());
				//si crea e aggiunge la sessione
				sessions_manager.addSession(socket);
			} catch (IOException e) {
				Log.error("errore nella connessione al server...");
			}
		}
	}
	
	/**
	 * Metodo che ferma il server
	 */
	public void stopPolyp(){
		//si salvano tutti i dati
		this.users_manager.writeUsers();
		//si ferma il ciclo
		this.run_polyp_server= false;
	}

}
