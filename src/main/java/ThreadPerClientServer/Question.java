package ThreadPerClientServer;

import java.util.Vector;

/**
 * Created by nadav on 09/01/16.
 */
public class Question {
    private String question;
    private String answer;
    private Vector<String> bluffAnswers;

    public Question(String question,String answer) {
        this.question = question;
        this.answer=answer;
        bluffAnswers=new Vector<>();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Question(question,answer);
    }

    public void addAnswer(String ans){
        bluffAnswers.add(ans);
    }

}
