package it.valsecchi.polypserver.data;

/**
 * Rappresenta un file nell'applicazione
 * @author Davide
 *
 */
public class PolypFile {

	private String userid;
	private String filename;
	
	public PolypFile(String userid,String filename){
		this.userid= userid;
		this.filename= filename;
	}

	public String getUserid() {
		return userid;
	}

	public String getFilename() {
		return filename;
	}
}
