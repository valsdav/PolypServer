package it.valsecchi.polypserver.connection;

import java.io.IOException;
import java.net.ServerSocket;
import it.valsecchi.polypserver.data.UsersManager;
import static it.valsecchi.polypserver.Utility.Log;

/**
 * Oggetto server.
 * @author Davide
 *
 */
public class PolypServer {

	private UsersManager usersManager;
	private ServerSocket polyp_server;
	private String polyp_name;
	private int port;
	private int max_clients;
	
	public PolypServer(String polyp_name,int port, int max_clients){
		this.polyp_name = polyp_name;
		this.port = port;
		this.max_clients= max_clients;
		//inizializzo il server
		try {
			polyp_server = new ServerSocket(this.port,this.max_clients);
		} catch (IOException e) {
			Log.error("Errore creazione polyp_server: " this.polyp_name);
		}
	}
	
}
