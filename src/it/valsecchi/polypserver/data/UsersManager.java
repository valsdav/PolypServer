package it.valsecchi.polypserver.data;

import it.valsecchi.polypserver.PolypServer;
import it.valsecchi.polypserver.exception.WrongPasswordException;
import static it.valsecchi.polypserver.Utility.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
		usersMap = new HashMap<>();
		this.loadUsers();
	}

	/** Metodo che legge gli users del server da file users_data */
	private void loadUsers() {
		// si legge il file Users
		Document doc = new Document();
		if (Files.exists(Paths.get(server.polyp_path
				+ "\\users\\users_data.xml"))) {
			// solo se esiste si legge se no si lascia così
			SAXBuilder build = new SAXBuilder();
			try {
				doc = build
						.build(server.polyp_path + "\\users\\users_data.xml");
			} catch (JDOMException | IOException e) {
				Log.error("errore lettura dati utenti");
			}
		}
		// ora si leggono i dati e si creano gli user
		for (Element e : doc.getRootElement().getChildren("user")) {
			this.addUser(new User((String) e.getChildText("id"), (String) e
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

	public void activateUser(String userid) {
		if (usersMap.containsKey(userid)) {
			usersMap.get(userid).setOnline(true);
		}
	}

	public boolean isUserAvaiable(String userid) {
		if (usersMap.containsKey(userid)) {
			return usersMap.get(userid).isOnline();
		} else {
			return false;
		}
	}

}
