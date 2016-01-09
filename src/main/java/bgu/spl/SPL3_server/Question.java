package bgu.spl.SPL3_server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

;
/**
 * Created by nadav on 09/01/16.
 */
public class Question {
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    private String question;
    private String answer;


    private Map<String,ServerProtocol> bluffAnswers;
    private List<Integer> perm;

    public Question(String question,String answer) {
        this.question = question;
        this.answer=answer;
        bluffAnswers=new HashMap<>();
    }

    @Override
    public Question clone(){
        return new Question(question,answer);
    }

    public void addAnswer(String ans,ServerProtocol protocol){
        bluffAnswers.put(ans,protocol);
    }

    public static Vector<Question> parseJson(String jsonPath) throws IOException, ParseException {
        Vector<Question>questions=new Vector<>();
        FileReader reader = new FileReader(jsonPath);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

        // get an array from the JSON object
        JSONArray questionsJsonArray= (JSONArray) jsonObject.get("questions");

        Iterator questionsArrayIterator = questionsJsonArray.iterator();

        // take each value from the json array separately
        while (questionsArrayIterator.hasNext()) {
            JSONObject questionJson = (JSONObject) questionsArrayIterator.next();
            questions.add(new Question((String)questionJson.get("questionText"),(String)questionJson.get("realAnswer")));
        }

        return questions;
    }

    public String getAnswerShuffle(){
        if(perm==null){
            perm = new ArrayList<Integer>();
            for (int i = 0; i < bluffAnswers.size(); i++) {
                perm.add(i);
            }
            perm.add(-1);
            java.util.Collections.shuffle(perm);
        }
        String shuffeledAnswers="";
        for (int i = 0; i < perm.size(); i++) {
            shuffeledAnswers+=i+". "+((perm.get(i)==-1)?answer:bluffAnswers.keySet().toArray()[perm.get(i)])+"    ";
        }
        return shuffeledAnswers;
    }
    public void selectAnswer(int choice, ServerProtocol player){
        if(perm.get(choice)==-1){
            player.addPoints(10);
        }
        else
        {
            bluffAnswers.get(bluffAnswers.keySet().toArray()[perm.get(choice)]).addPoints(5);
        }
    }
}
