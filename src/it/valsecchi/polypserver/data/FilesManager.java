package it.valsecchi.polypserver.data;

import static it.valsecchi.polypserver.Utility.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import it.valsecchi.polypserver.PolypServer;

/**
 * Gestisce i files dei vari utenti
 * 
 * @author Davide
 * 
 */
public class FilesManager {

	private Map<String, List<String>> filesMap;
	private PolypServer server;

	public FilesManager(PolypServer server) {
		this.server = server;
		//si caricano i dati
		this.loadFiles();
	}

	/** Metodo che legge i file dal server del server da file files_data */
	private void loadFiles() {
		// si legge il file Users
		Document doc = new Document();
		File file = new File(server.polyp_path
				+ "\\files\\files_data.xml");
		if (file.exists()) {
			// solo se esiste si legge se no si lascia così
			SAXBuilder build = new SAXBuilder();
			try {
				doc = build
						.build(server.polyp_path + "\\files\\files_data.xml");
			} catch (JDOMException e) {
				Log.error("errore lettura dati file");
			}catch( IOException e1){
				Log.error("errore lettura dati file");
			}
		}
		// ora si leggono i dati e si creano gli user
		for (Element e : doc.getRootElement().getChildren("user_id")) {
			for (Element e2 : e.getChildren("file")) {
				this.addFile(e.getAttributeValue("id"), e2.getText());
			}
		}
	}
	
	/** Metodo che scrive i dati degli files nel file files_data */
	public void writeFiles() {
		// si crea un document e lo si scrive
		Document doc = new Document();
		doc.setRootElement(new Element("files_data"));
		for (String u:filesMap.keySet()){
			Element e = new Element("user_id");
			e.setAttribute(new Attribute("id",u));
			for(String f: filesMap.get(u)){
				Element fi = new Element("file").setText(f);
				e.addContent(fi);
			}
			doc.getRootElement().addContent(e);
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		FileOutputStream output;
		try {
			output = new FileOutputStream(server.polyp_path
					+ "\\files\\files_data.xml");
			outputter.output(doc, output);
		} catch (IOException e) {
			Log.error("errore scrittura dati files");
		}
	}

	public void addFile(String user_id, String file) {
		if (filesMap.containsKey(user_id)) {
			filesMap.get(user_id).add(file);
		} else {
			filesMap.put(user_id, new ArrayList<String>());
		}
	}
	
	public void setFileList(String user_id, List<String> files){
		if(filesMap.containsKey(user_id)){
			filesMap.get(user_id).clear();
			filesMap.get(user_id).addAll(files);
		}else{
			filesMap.put(user_id, files);
		}
	}

	public Map<String, List<String>> getFileList(){
		return filesMap;
	}
	
}
