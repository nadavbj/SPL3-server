package ThreadPerClientServer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ServerProtocolImpl implements ServerProtocol<String>  {

	private String name;
	public void setName(String name) {
		this.name = name;
	}

	private final String nick="NICK";
	private final String join="JOIN";
	private final String start="STARTGAME";


	public String processMessage(String msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processMessage(String msg, ProtocolCallback<String> callback) {
		
		try {

			/** NICK  **/
			if(msg.startsWith(nick)) 
				//the user is already in use
				if (ServerData.instance.getUsuer2room().containsKey(msg.substring(5))){
					callback.sendMessage("SYSMSG NICK REJECTED, "+ msg.substring(5)+ "is already in use");
				}
			//Creates a new user
				else {
					setName(msg.substring(5));
					callback.sendMessage("SYSMSG NICK ACCEPTED");}


			/** JOIN  **/
			if(msg.startsWith(join)){
				
				if(ServerData.instance.getUsuer2room().containsKey(name) && ServerData.instance.getUsuer2room().get(name).isActive()){
					callback.sendMessage("SYSMSG JOIN REJECTED, you can't leave in the middle of a game");
					
				}

				//the room is already active
				else if ((ServerData.instance.getRoomName2room().containsKey(msg.substring(5))) &&(ServerData.instance.getRoomName2room().get(msg.substring(5)).isActive()) ){
					callback.sendMessage("SYSMSG JOIN REJECTED, "+msg.substring(5)+ "is already active");
				}
				//the room is already exist, and not active
				else if ((ServerData.instance.getRoomName2room().containsKey(msg.substring(5))) && !(ServerData.instance.getRoomName2room().get(msg.substring(5)).isActive()) ){
					ServerData.instance.getRoomName2room().get(msg.substring(5)).add(name);
					ServerData.instance.getUsuer2room().put(name, ServerData.instance.getRoomName2room().get(msg.substring(5)));
					//ServerData.instance.getRoom2users().
					callback.sendMessage("SYSMSG JOIN ACCEPTED");
				}
				//the room doesn't exist, will open a new one
				else{
					Room newRoom= new Room(msg.substring(5), new HashSet<String>(), false);
					ServerData.instance.getRoomName2room().put(msg.substring(5), newRoom);
					ServerData.instance.getRoomName2room().get(msg.substring(5)).add(name);
					ServerData.instance.getUsuer2room().put(name, newRoom);
					callback.sendMessage("SYSMSG JOIN ACCEPTED");
				}
			}

			/** STARTGAME  **/


			if (msg.startsWith(start)){
				
				if (msg.substring(10).equals("BLUFFER")){
					ServerData.instance.getUsuer2room().get(name).setActive(true);
					String roomName= ServerData.instance.getUsuer2room().get(name).getRoomName();
					ServerData.instance.getRoomName2room().get(roomName).setActive(true);
					callback.sendMessage("SYSMSG STARTGAME ACCEPTED");
				}
				else callback.sendMessage("SYSMSG STARTGAME REJECTED, "+ "we don't have the game " + msg.substring(9));
			}



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean isEnd(String msg) {
		// TODO Auto-generated method stub
		return false;
	}
}
