package it.valsecchi.polypserver.data;

import java.util.List;
import java.util.Map;

import it.valsecchi.polypserver.PolypServer;

/**
 * Gestisce i files dei vari utenti
 * 
 * @author Davide
 * 
 */
public class FilesManager {
	
	private Map<String,List<PolypFile>> filesMap;
	private PolypServer server;

	public FilesManager(PolypServer server) {
		this.server = server;
	}

}
