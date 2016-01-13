package bgu.spl.SPL3_server;


import ThreadPerClientServer.ProtocolCallback;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Room{
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

    public boolean isActive() {
        return isActive;
    }

    private int usersWhoHittedStart=0;
    public void userHittedStart() {
        usersWhoHittedStart++;
        if(usersWhoHittedStart==users.size()){
            isActive=true;
            askQuestion();
        }
    }

    public void sendMSG(String message,ServerProtocol sender){
        users.stream().filter(user -> user != sender).forEach(user -> {
            user.sendMessage("USRMSG new message from " + sender.getName() + ": " + message, null, null);
        });


    }
    private int awaitingAnswers;
    private int leftQuestions=1;
    Object lockedObj=new Object();

    private void askQuestion(){
        leftQuestions--;
        awaitingAnswers=users.size();
        Question q=ServerData.instance.getQuestion();

        for (ServerProtocol protocol:users) {
            protocol.sendMessage("ASKTXT "+q.getQuestion(),"TXTRESP", new ProtocolCallback<String>() {
                @Override
                public void sendMessage(String ans) throws IOException {
                    q.addAnswer(ans, protocol);
                    synchronized (lockedObj) {
                        awaitingAnswers--;
                    }
                    if (awaitingAnswers == 0) {
                        Room.this.askChoices(q);
                    }
                }
            });
        }
    }
    void askChoices(Question q)
    {
        awaitingAnswers=users.size();
        for (ServerProtocol protocol:users) {
            protocol.sendMessage("ASKCHOICES "+q.getAnswerShuffle(),"SELECTRESP", new ProtocolCallback<String>() {
                @Override
                public void sendMessage(String ans) throws IOException {
                    q.selectAnswer(Integer.parseInt(ans.trim()), protocol);
                    synchronized (lockedObj) {
                        awaitingAnswers--;
                    }

                    if(awaitingAnswers==0)
                        if(leftQuestions==0)
                            finish();
                        else
                        {
                            askQuestion();
                        }
                }
            });
        }
    }
    private void finish() {
        int maxPoints = 0;
        for (ServerProtocol user : users) {
            if (user.getPoints() >= maxPoints) {
                maxPoints = user.getPoints();
            }
        }
        for (ServerProtocol user : users) {
            if (user.getPoints() == maxPoints) {
                user.sendMessage("GAMEMSG You won!", null, null);
            } else {
                user.sendMessage("GAMEMSG You losed! ha ha", null, null);
            }
        }
        isActive = false;
        for (ServerProtocol user : users) {
            ServerData.instance.getUsuer2room().replace(user.getName(), null);
        }
        users.clear();
        usersWhoHittedStart=0;
        leftQuestions=1;
    }
}
