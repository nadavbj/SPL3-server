package bgu.spl.SPL3_server;


import java.util.HashSet;
import java.util.Set;

public class Room implements Runnable {
    private String roomName;
    private Set<ServerProtocol> users;
    private boolean isActive;

    public Room(String roomName) {
        this.roomName = roomName;
        this.isActive = false;
        users=new HashSet<>();
        System.out.println("room created");
    }

    public void add(ServerProtocol user){
        users.add(user);

    }

    public String getRoomName() {
        return roomName;
    }

    public Set<ServerProtocol> getUsers() {
        return users;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setUsers(Set<ServerProtocol> users) {
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

    public void sendMSG(String message,ServerProtocol sender){
        users.stream().filter(user -> user != sender).forEach(user -> {
            user.getConnectionHandler().sendMessage("USRMSG new message from " + sender.getName() + ": " + message, null, null);
        });


    }


    @Override
    public void run() {
        isActive=true;
        for (int j = 0; j <3 ; j++)
        {
            Question q=ServerData.instance.getQuestion();

            Object lockedObj=new Object();
            for (ServerProtocol protocol:users) {
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
            for (ServerProtocol protocol:users) {
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
        for(ServerProtocol user : users) {
            ServerData.instance.getUsuer2room().replace(user.getName(),null);
        }
        users.clear();
    }
}
