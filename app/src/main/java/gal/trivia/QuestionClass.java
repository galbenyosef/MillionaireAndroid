package gal.trivia;

import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionClass {

    String question,difficulty,correct_answer;
    ArrayList<String> answers;
    int Serial;

    public QuestionClass(String diff,String _qst,String ans0, String[] ans){

        difficulty=diff;
        answers = new ArrayList<String>();
        question=_qst;
        correct_answer=ans0;
        answers.add(ans0);
        for (int i = 0 ; i < ans.length; i++) {
            answers.add(ans[i]);
        }
        serialize();
    }

    public void shuffle(){
        Collections.shuffle(answers);
    }


    private void serialize(){

        switch (difficulty){
            case "easy":
                Serial=1;
                break;
            case "medium":
                Serial=2;
                break;
            case "hard":
                Serial=3;
                break;
        }
    }

    static ArrayList<QuestionClass> JSONtoQuestions(JSONObject json) {

        ArrayList<QuestionClass> questions = new ArrayList<QuestionClass>();
        JSONArray results, other_answers;
        JSONObject result_obj;
        String question, correct_ans, difficulty, other_ans[];
        question = correct_ans = null;other_ans = null;

        try {
            results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                result_obj = results.getJSONObject(i);
                question = StringEscapeUtils.unescapeHtml4(result_obj.getString("question"));
                difficulty = StringEscapeUtils.unescapeHtml4(result_obj.getString("difficulty"));
                correct_ans = StringEscapeUtils.unescapeHtml4(result_obj.getString("correct_answer"));
                other_answers = result_obj.getJSONArray("incorrect_answers");
                other_ans = new String[other_answers.length()];
                for (int j = 0; j < other_ans.length; j++) {
                    other_ans[j] = StringEscapeUtils.unescapeHtml4(other_answers.get(j).toString());
                }
                questions.add(new QuestionClass(difficulty,question,correct_ans,other_ans));
                Log.d("question",question);
            }

        } catch (Exception e) {

        }
        return questions;
    }

    public int getSerial(){return Serial;}
    public String getQuestion(){ return question; }
    public String getAnswer(int i){return answers.get(i);}
    public String getCorrectAnswer(){return correct_answer;}
    public String getDifficulty(){return difficulty;}
}