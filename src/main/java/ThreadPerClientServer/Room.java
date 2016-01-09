package ThreadPerClientServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Room implements Runnable {
	private String roomName;
	private Set<ServerProtocolImpl> users;
	private boolean isActive;
	private Map<ServerProtocolImpl,String> answerPerPlayer;
	
	public Room(String roomName, Set<ServerProtocolImpl> users, boolean isActive) {
		this.roomName = roomName;
		this.users = users;
		this.isActive = isActive;
		answerPerPlayer =new HashMap();
	}
	
	public void add(ServerProtocolImpl user){
		users.add(user);
		
	}

	public String getRoomName() {
		return roomName;
	}

	public Set<ServerProtocolImpl> getUsers() {
		return users;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public void setUsers(Set<ServerProtocolImpl> users) {
		this.users = users;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	@Override
	public void run() {
		for (ServerProtocolImpl protocol:users) {
			protocol.processMessage("ASKTXT "+question,ans->{answerPerPlayer.put(protocol,ans);});
		}
	}
}
