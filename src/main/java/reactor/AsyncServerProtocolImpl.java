package reactor;

import ThreadPerClientServer.*;
import bgu.spl.SPL3_server.AsyncServerProtocol;
import bgu.spl.SPL3_server.ConnectionHandler;
import bgu.spl.SPL3_server.Room;
import bgu.spl.SPL3_server.ServerData;

import java.io.IOException;
import java.util.HashSet;

/**
 * Created by nadav on 09/01/16.
 */
public class AsyncServerProtocolImpl implements AsyncServerProtocol<String> {
    private String name;
    private int points=0;
    private boolean shouldClose=false;
    private boolean isTerminated=false;

    public void setName(String name) {
        this.name = name;
        ServerData.instance.getUsuer2room().put(name,null);
    }
    public String getName(){return name;}

    private ConnectionHandler connectionHandler;

    private final String nick="NICK";
    private final String join="JOIN";
    private final String start="STARTGAME";
    private final String ASKTXT="ASKTXT";
    private final String MSG="MSG";





    @Override
    public void setConnection(ConnectionHandler connection) {
        connectionHandler=connection;
    }

    @Override
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public void addPoints(int points) {
        getConnectionHandler().sendMessage("GAMEMSG you recived "+points+" points!",null,null);
        this.points+=points;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void processMessage(String msg, ProtocolCallback<String> callback) {
        if(isTerminated)
        {
            System.out.println("Terminated, cant proccess message.");
            return;
        }
        msg=msg.trim();
        try {

            /** NICK  **/
            if(msg.startsWith(nick))
                //the user is already in use
                if (ServerData.instance.getUsuer2room().containsKey(msg.substring(nick.length()+1))){
                    callback.sendMessage("SYSMSG NICK REJECTED, "+ msg.substring(nick.length()+1)+ " is already in use");
                }
                //Creates a new user
                else {
                    setName(msg.substring(nick.length()+1));
                    callback.sendMessage("SYSMSG NICK ACCEPTED");}


            /** JOIN  **/
            if(msg.startsWith(join)){

                if(ServerData.instance.getUsuer2room().containsKey(name) &&ServerData.instance.getUsuer2room().get(name)!=null&& ServerData.instance.getUsuer2room().get(name).isActive()){
                    callback.sendMessage("SYSMSG JOIN REJECTED, you can't leave in the middle of a game");

                }

                //the room is already active
                else if ((ServerData.instance.getRoomName2room().containsKey(msg.substring(join.length()+1))) &&(ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)).isActive()) ){
                    callback.sendMessage("SYSMSG JOIN REJECTED, "+msg.substring(join.length()+1)+ "is already active");
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
                    Room newRoom= new Room(msg.substring(join.length()+1), false);
                    ServerData.instance.getRoomName2room().put(msg.substring(join.length()+1), newRoom);
                    ServerData.instance.getRoomName2room().get(msg.substring(join.length()+1)).add(this);
                    ServerData.instance.getUsuer2room().replace(name, newRoom);
                    callback.sendMessage("SYSMSG JOIN ACCEPTED");
                }

            }

            /** LISTGAMES  **/
            if (msg.equals("LISTGAMES")){
                callback.sendMessage("SYSMSG LISTGAMES ACCEPTED: 1.BLUFFER");
            }




            /** STARTGAME  **/


            if (msg.startsWith(start)){
                points=0;
                if (msg.substring(start.length()+1).equals("BLUFFER")){
                    ServerData.instance.getUsuer2room().get(name).userHittedStart();
                    String roomName= ServerData.instance.getUsuer2room().get(name).getRoomName();
                    //TODO:ServerData.instance.getRoomName2room().get(roomName).setActive();
                    callback.sendMessage("SYSMSG STARTGAME ACCEPTED");
                }
                else callback.sendMessage("SYSMSG STARTGAME REJECTED, "+ "we don't have the game " + msg.substring(9));
            }

            if(msg.startsWith(MSG)){
                String m= msg.substring(MSG.length()+1);
                if(ServerData.instance.getUsuer2room().get(name)!=null){
                    ServerData.instance.getUsuer2room().get(name).sendMSG(m,this);
                }
            if(msg.equals("QUIT"))
                shouldClose=true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //TODO: delete
    @Override
    public String processMessage(String msg) {
        return null;
    }

    @Override
    public boolean isEnd(String msg) {
        return msg.equals("QUIT")||shouldClose||isTerminated;
    }

    @Override
    public boolean shouldClose() {
        return shouldClose;
    }

    @Override
    public void connectionTerminated() {
        isTerminated=true;
    }
}
