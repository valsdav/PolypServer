package it.valsecchi.polypserver.data;

public class User {
	
	private String ID;
	private String username;
	
	public User(String id, String name){
		ID = id;
		setUsername(name);
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
