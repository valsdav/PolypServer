package it.valsecchi.polypserver.data;

public class User {
	
	private String ID;
	private String username;
	private boolean online;
	
	public User(String id, String username){
		ID = id;
		this.username = username;
		this.online= false;
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

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

}
