package ThreadPerClientServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Room implements Runnable {
    private String roomName;
    private Set<ServerProtocolImpl> users;
    private boolean isActive;

    public Room(String roomName, Set<ServerProtocolImpl> users, boolean isActive) {
        this.roomName = roomName;
        this.users = users;
        this.isActive = isActive;
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

    private int usersWhoHittedStart=0;
    public void userHittedStart() {
        usersWhoHittedStart++;
        if(usersWhoHittedStart==users.size()){
            isActive=true;
            new Thread(this).start();
        }
    }

    public void sendMSG(String message,ServerProtocolImpl sender){
        for (ServerProtocolImpl user : users
                ) {
            if(user!=sender){
                user.getConnectionHandler().sendMessage("new message from " + sender.getName() +": "+message,null,null);
            }

        }


    }


    @Override
    public void run() {
        for (int j = 0; j <1 ; j++)
        {
            Question q=ServerData.instance.getQuestion();
            Object lockedObj=new Object();
            for (ServerProtocolImpl protocol:users) {
                protocol.getConnectionHandler().sendMessage("ASKTXT "+q.getQuestion(),"TXTRESP",(String ans)->{
                    q.addAnswer(ans,protocol);
                    synchronized (lockedObj){
                        lockedObj.notify();
                    }

                });
            }
            for (int i = 0; i < users.size(); i++) {
                try {
                    synchronized (lockedObj){
                        lockedObj.wait();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (ServerProtocolImpl protocol:users) {
                protocol.getConnectionHandler().sendMessage("ASKCHOICES "+q.getAnswerShuffle(),"SELECTRESP",(String ans)->{
                    q.selectAnswer(Integer.parseInt(ans.trim()),protocol);
                    synchronized (lockedObj){
                        lockedObj.notify();
                    }
                });
            }
            for (int i = 0; i < users.size(); i++) {
                try {
                    synchronized (lockedObj){
                        lockedObj.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        int maxPoints=0;
        for (ServerProtocol user : users) {
            if(user.getPoints()>=maxPoints){
                maxPoints=user.getPoints();
            }
        }
        for(ServerProtocol user : users) {
            if(user.getPoints()==maxPoints){
                user.getConnectionHandler().sendMessage("GAMEMSG You won!",null,null);
            }
            else
            {
                user.getConnectionHandler().sendMessage("GAMEMSG You losed! ha ha",null,null);
            }
        }
        isActive=false;
        for(ServerProtocolImpl user : users) {
            ServerData.instance.getUsuer2room().replace(user.getName(),null);
        }
        users.clear();
    }
}
