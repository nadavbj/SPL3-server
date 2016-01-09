package ThreadPerClientServer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ServerData {
	private Map<String,Room> usuer2room;
	private Map<String,Room> roomName2room;


	private ServerData(){
		usuer2room= new HashMap();
		roomName2room=  new HashMap();
	}
	
	public static ServerData instance=new ServerData();
	
	
	public Map<String, Room> getRoomName2room() {
		return roomName2room;
	}

	public void setRoomName2room(Map<String, Room> roomName2room) {
		this.roomName2room = roomName2room;
	}
	public Map<String, Room> getUsuer2room() {
		return usuer2room;
	}

	public void setUsuer2room(Map<String, Room> usuer2room) {
		this.usuer2room = usuer2room;
	}
	


	public String getQuestion(){

	}

}
