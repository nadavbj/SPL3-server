package bgu.spl.SPL3_server;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;


public class ServerData {
	private Map<String,Room> usuer2room;
	private Map<String,Room> roomName2room;
	private Vector<Question> questions;

	private ServerData(){
		usuer2room= new HashMap();
		roomName2room=  new HashMap();
	}
	
	public static ServerData instance=new ServerData();
	
	
	public Map<String, Room> getRoomName2room() {
		return roomName2room;
	}

	public Map<String, Room> getUsuer2room() {
		return usuer2room;
	}

	public void loadQuestionsFromJson(String jsonPath) throws IOException, ParseException {
		questions=Question.parseJson(jsonPath);
	}
	private int questionNumber=0;
	public Question getQuestion(){
		return questions.get((questionNumber++)%questions.size()).clone();
	}

}
