package gal.trivia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;

public class GameActivity extends AppCompatActivity {

    String GameType, GameCategory;
    int cur_question;
    int last_question = 13;
    TextView questionText,timer_view;
    ArrayList<Button> answers_buttons;
    View.OnClickListener button_clicks,button_empty_clicks;
    ArrayList<QuestionClass> game_questions;
    Handler handler;
    Object waitForAPI;
    CountDownTimer timer;
    RelativeLayout fragment_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        questionText = (TextView) findViewById(R.id.question);
        answers_buttons = new ArrayList<Button>();
        handler = new Handler();
        waitForAPI = new Object();
        answers_buttons.add((Button) findViewById(R.id.button_ans_1));
        answers_buttons.add((Button) findViewById(R.id.button_ans_2));

        game_questions = new ArrayList<QuestionClass>();

        Intent gameDetails = getIntent();
        GameType = gameDetails.getStringExtra("type");
        GameCategory = gameDetails.getStringExtra("category");

        if (GameType.equalsIgnoreCase("multiple")) {
            answers_buttons.add((Button) findViewById(R.id.button_ans_3));
            answers_buttons.add((Button) findViewById(R.id.button_ans_4));
        }
        else{
            last_question=5;
            findViewById(R.id.bottomLeftDownLayout).setVisibility(View.GONE);
            findViewById(R.id.bottomRightDownLayout).setVisibility(View.GONE);
        }

        cur_question = 0;

        button_clicks = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Button b = (Button)view;
                timer.cancel();
                if (b.getText().toString().equalsIgnoreCase(game_questions.get(cur_question).getCorrectAnswer())){
                    b.clearAnimation();
                    b.startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.animate_win));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roundWin();
                        }
                    });
                }
                else {
                    for (Button btn : answers_buttons) {
                        if (!btn.getText().toString().equalsIgnoreCase(game_questions.get(cur_question).getCorrectAnswer())){
                            btn.clearAnimation();
                            btn.startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.animate_lose));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roundLose();
                        }
                    });
                }
                timer.cancel();
            }
        };
        button_empty_clicks = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do n0thing
            }
        };

        fragment_layout=(RelativeLayout)findViewById(R.id.gameLayoutRight);

        timer_view=(TextView)findViewById(R.id.timer);
        timer = new CountDownTimer(30000, 1000) {
            public void onTick(final long millisUntilFinished) {
                int progress = (int) (millisUntilFinished/1000);
                timer_view.setText(Integer.toString(progress));
            }
            public void onFinish() {
                cancel();
                roundLose();
            }
        };

        getQuestions();
    }

    void setClickables(){
        for (Button b : answers_buttons){
            b.setOnClickListener(button_clicks);
        }
    }
    void unsetClickables(){
        for (Button b : answers_buttons){
            b.setOnClickListener(button_empty_clicks);
        }
    }

    void getQuestions(){

        fetchQuestions();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (waitForAPI){
                        Log.d("a","wait...");
                        waitForAPI.wait();
                        gamePrepared();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (waitForAPI) {
                    while (game_questions.size()<last_question);
                    Log.d("a","ready...");
                    waitForAPI.notify();
                }
            }
        }).start();


    }

    void gamePrepared(){
        Audio.stopSound();
        Collections.sort(game_questions,new Comparator<QuestionClass>() {
            @Override
            public int compare(QuestionClass t0, QuestionClass t1) {
                return t0.getSerial()-(t1.getSerial());
            }
        });
        shuffleQuestionsAnswers();
        gamePlayProgress();
    }

    void gamePlayProgress() {
        ScoreFragment sr = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prepare4Question();
            }
        });
        while (sr == null) {
            sr = (ScoreFragment) getSupportFragmentManager().findFragmentByTag("score");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setQuestion();
            }
        }, 4000);
    }

    void shuffleQuestionsAnswers(){
        for (QuestionClass q : game_questions){
            q.shuffle();
        }
    }
    void prepare4Question(){
        Audio.playSound(GameActivity.this,R.raw.b4question);
        unsetClickables();
        hideQuestion();
        timer.cancel();
        fragment_layout.setVisibility(View.VISIBLE);
        timer_view.setVisibility(View.GONE);
        callScores();
    }

    void callScores(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction incoming_fragment = getSupportFragmentManager().beginTransaction();
                incoming_fragment.replace(R.id.gameLayoutRight, ScoreFragment.getInstance(getApplicationContext(),cur_question), "score").commit();
            }
        }).start();
    }

    void setQuestion(){
        Audio.stopSound();
        Audio.playSound(GameActivity.this, R.raw.gameprogress);
        fragment_layout.setVisibility(View.GONE);
        timer_view.setVisibility(View.VISIBLE);
        showQuestion();
        timer.start();
        setClickables();
    }

    void showQuestion(){
        questionText.setText(game_questions.get(cur_question).getQuestion());
        for (int i =0; i<answers_buttons.size();i++)
            answers_buttons.get(i).setText(game_questions.get(cur_question).getAnswer(i));
    }

    void hideQuestion(){
            questionText.setText("");
            for (int i =0; i<answers_buttons.size();i++)
                answers_buttons.get(i).setText("");
    }

    void roundWin(){
        unsetClickables();
        Audio.stopSound();
        Audio.playSound(this,R.raw.win);
        cur_question++;
        if (cur_question==last_question){
            cur_question=0;
            gameOver();
        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gamePrepared();

                }
            }, 6000);
        }
    }

    void roundLose(){
        unsetClickables();
        Audio.stopSound();
        Audio.playSound(this,R.raw.lose);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameOver();

            }
        }, 6000);;
    }

    void gameOver(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setMessage("Would you like to try again?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gamePrepared();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Audio.stopSound();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    void fetchQuestions() {
        int amount,category;
        String difficulty,type;
        amount=category=0;
        difficulty=null;
        type=GameType;

        switch (GameCategory) {
            case "General":
                category=9;
                break;
            case "Sports":
                category=21;
                break;
            case "Computers":
                category=18;
                break;
            case "Mathematics":
                category=19;
                break;
            case "Mythology":
                category=20;
                break;
        }

        for (int i=0; i<3;i++) {
            if (i == 0) {
                amount = 5;
                difficulty = "easy";
            } else if (i == 1) {
                amount = 5;
                difficulty = "medium";
            } else {
                amount = 3;
                difficulty = "hard";
            }
            fetch(amount, category, difficulty, type);
        }
    }

    private void fetch(int amount,int cat,String diff,String type) {;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("amount",amount);
        params.put("category",cat);
        params.put("difficulty",diff);
        params.put("type",type);

        client.get("http://opentdb.com/api.php?",params,new JsonHttpResponseHandler(){


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                game_questions.addAll(QuestionClass.JSONtoQuestions(response));
                Log.d("questions size",String.valueOf(game_questions.size()));
            }
        });
    }
}
