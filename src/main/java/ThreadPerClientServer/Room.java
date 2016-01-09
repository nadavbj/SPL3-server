package ThreadPerClientServer;

import java.util.Set;

public class Room {
	private String roomName;
	private Set<String> users;
	private boolean isActive;
	
	public Room(String roomName, Set<String> users, boolean isActive) {
		this.roomName = roomName;
		this.users = users;
		this.isActive = isActive;
		
	}
	
	public void add(String user){
		users.add(user);
		
	}

	public String getRoomName() {
		return roomName;
	}

	public Set<String> getUsers() {
		return users;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
	
	

}
