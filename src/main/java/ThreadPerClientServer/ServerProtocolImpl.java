package ThreadPerClientServer;


import bgu.spl.SPL3_server.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerProtocolImpl implements ServerProtocol<String> {

	private String name;
	private int points=0;
	private ProtocolCallback<String>callback;
	private Map<String, ProtocolCallback> responsesCallBacks = new HashMap();

	public void setName(String name) {
		this.name = name;
		ServerData.instance.getUsuer2room().put(name,null);
	}

	@Override
	public void sendMessage(String message, String responseCommannd, ProtocolCallback<String> callback) {
		try {
			this.callback.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(responseCommannd!=null)
			responsesCallBacks.put(responseCommannd,callback);
	}

	public String getName(){return name;}

	private final String nick="NICK";
	private final String join="JOIN";
	private final String start="STARTGAME";
	private final String MSG="MSG";
	protected final String Quit="QUIT";






	@Override
	public void addPoints(int points) {
		try {
			callback.sendMessage("GAMEMSG you recived "+points+" points!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.points+=points;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public void processMessage(String msg, ProtocolCallback<String> callback) {

		try {
			String command;
			if(msg.contains(" "))
				command=msg.substring(0,msg.indexOf(" "));
			else
				command=msg;
			this.callback=callback;
			if(responsesCallBacks.containsKey(command)){
				responsesCallBacks.get(command).sendMessage(msg.substring(msg.indexOf(" ")));
				responsesCallBacks.remove(command);
				return;
			}
			msg=msg.trim();
			/** NICK  **/
			if(command.equals(nick) && name==null) {
				//the user is already in use
				if (ServerData.instance.getUsuer2room().containsKey(msg.substring(nick.length() + 1))) {
					callback.sendMessage("SYSMSG NICK REJECTED, " + msg.substring(nick.length() + 1) + " is already in use");
				}
				//Creates a new user
				else {
					setName(msg.substring(nick.length() + 1));
					callback.sendMessage("SYSMSG NICK ACCEPTED");
				}
				return;
			}

			/** JOIN  **/
			if(command.equals(join)){

				if(ServerData.instance.getUsuer2room().containsKey(name) &&ServerData.instance.getUsuer2room().get(name)!=null&& ServerData.instance.getUsuer2room().get(name).isActive()){
					callback.sendMessage("SYSMSG JOIN REJECTED, you can't leave in the middle of a game");

				}

				//the room is already active
				else if ((ServerData.instance.getRoomName2room().containsKey(msg.substring(join.length()+1))) &&(ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)).isActive()) ){
					callback.sendMessage("SYSMSG JOIN REJECTED, "+msg.substring(join.length()+1)+ " is already active");
				}
				//the room is already exist, and not active
				else if ((ServerData.instance.getRoomName2room().containsKey(msg.substring(join.length()+1))) && !(ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)).isActive()) ){
					ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)).add(this);
					ServerData.instance.getUsuer2room().replace(name, ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)));
					//ServerData.instance.getRoom2users().
					callback.sendMessage("SYSMSG JOIN ACCEPTED");
				}
				//the room doesn't exist, will open a new one
				else{
					Room newRoom= new Room(msg.substring(join.length()+1));
					ServerData.instance.getRoomName2room().put(msg.substring(join.length()+1), newRoom);
					ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)).add(this);
					ServerData.instance.getUsuer2room().replace(name, newRoom);
					callback.sendMessage("SYSMSG JOIN ACCEPTED");
				}
				return;
			}

			/** LISTGAMES  **/
			if (msg.equals("LISTGAMES")){
				callback.sendMessage("SYSMSG LISTGAMES ACCEPTED: 1.BLUFFER");
				return;
			}




			/** STARTGAME  **/


			if (command.equals(start)){
				points=0;
				if (msg.substring(start.length()+1).equals("BLUFFER")){
					callback.sendMessage("SYSMSG STARTGAME ACCEPTED");
					ServerData.instance.getUsuer2room().get(name).userHittedStart();
				}
				else callback.sendMessage("SYSMSG STARTGAME REJECTED, "+ "we don't have the game " + msg.substring(9));
				return;
			}

			if(command.equals(MSG)){
				String m= msg.substring(MSG.length()+1);
				if(ServerData.instance.getUsuer2room().get(name)!=null){
					ServerData.instance.getUsuer2room().get(name).sendMSG(m,this);
				}

				return;
			}
			if(command.equals(Quit))
			{
				callback.sendMessage("SYSMSG QUIT ACCEPTED");
				return;
			}
			callback.sendMessage("SYSMSG "+msg+" UNIDENTIFIED");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isEnd(String msg) {
		return msg.equals(Quit);
	}
}
