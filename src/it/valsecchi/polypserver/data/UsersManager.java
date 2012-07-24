package it.valsecchi.polypserver.data;

import java.util.HashMap;
import java.util.Map;

public class UsersManager {

	private Map<String,User> usersMap;
	
	public UsersManager(){
		usersMap = new HashMap<>();
	}
	
	public void addUser(User user){
		if(usersMap.containsKey(user.getID())){
			usersMap.put(user.getID(),user);
		}
		
	}
	
}
