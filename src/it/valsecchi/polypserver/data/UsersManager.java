package it.valsecchi.polypserver.data;

import it.valsecchi.polypserver.PolypServer;
import it.valsecchi.polypserver.exception.WrongPasswordException;
import static it.valsecchi.polypserver.Utility.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class UsersManager {

	private Map<String, User> usersMap;
	private PolypServer server;

	public UsersManager(PolypServer server) {
		this.server = server;
		usersMap = new HashMap<String, User>();
		this.loadUsers();
	}

	/** Metodo che legge gli users del server da file users_data */
	private void loadUsers() {
		// si legge il file Users
		Document doc = new Document();
		File file = new File(server.polyp_path
				+ "\\users\\users_data.xml");
		if (file.exists()) {
			// solo se esiste si legge se no si lascia cos�
			SAXBuilder build = new SAXBuilder();
			try {
				doc = build
						.build(server.polyp_path + "\\users\\users_data.xml");
			} catch (JDOMException e) {
				Log.error("errore lettura dati utenti");
			}catch( IOException e1){
				Log.error("errore lettura dati utenti");
			}
		}
		// ora si leggono i dati e si creano gli user
		for (Element e : doc.getRootElement().getChildren("user")) {
			this.addUser(new User(e.getChildText("id"), e
					.getChildText("username")));
		}
	}

	/** Metodo che scrive i dati degli users nel file users_data */
	public void writeUsers() {
		// si crea un document e lo si scrive
		Document doc = new Document();
		doc.setRootElement(new Element("users_data"));
		for (User u : usersMap.values()) {
			Element e = new Element("user").addContent(new Element("id")
					.setText(u.getID()));
			e.addContent(new Element("username").setText(u.getUsername()));
			doc.getRootElement().addContent(e);
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		FileOutputStream output;
		try {
			output = new FileOutputStream(server.polyp_path
					+ "\\users\\users_data.xml");
			outputter.output(doc, output);
		} catch (IOException e) {
			Log.error("errore scrittura dati user");
		}
	}

	public void addUser(User user) {
		if (!usersMap.containsKey(user.getID())) {
			usersMap.put(user.getID(), user);
		} else {
		}
	}

	public void addUser(String userid, String username, String password)
			throws WrongPasswordException {
		// si controlla la password
		if (server.polyp_password.equals(password)) {
			// si aggiunge
			this.addUser(new User(userid, username));
		} else {
			throw new WrongPasswordException(userid);
		}
	}

	public boolean activateUser(String userid) {
		if (usersMap.containsKey(userid)) {
			if (usersMap.get(userid).isOnline() == true) {
				return false;
			} else {
				usersMap.get(userid).setOnline(true);
				return true;
			}
		} else {
			return false;
		}
	}

	public void deactivateUser(String userid) {
		if (usersMap.containsKey(userid)) {
			usersMap.get(userid).setOnline(false);
		}
	}

	public boolean isUserAvaiable(String userid) {
		if (usersMap.containsKey(userid)) {
			return usersMap.get(userid).isOnline();
		} else {
			return false;
		}
	}
	
	public List<String> getAvaibleUser(){
		List<String > us = new ArrayList<String>();
		for(User u: usersMap.values()){
			if(u.isOnline()== true){
				us.add(u.getID());
			}
		}
		return us;
	}

}
