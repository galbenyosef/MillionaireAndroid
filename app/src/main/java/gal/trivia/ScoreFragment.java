package gal.trivia;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class ScoreFragment extends Fragment {

    TextView current_score;
    Animation textHighlight;
    static int current_score_id;

    public ScoreFragment() {
    }

    public static ScoreFragment getInstance(Context c,int score) {
        current_score_id=score;
        ScoreFragment f = new ScoreFragment();
        return f;
    }

    public TextView getCurrentScore(){return current_score;}

    public void myAnimate(){
        current_score.clearAnimation();
        current_score.startAnimation(textHighlight);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_score, container, false);

        textHighlight = AnimationUtils.loadAnimation(v.getContext(), R.anim.animate_score);
        textHighlight.reset();
        switch (current_score_id){
            case 0:
                current_score = (TextView)v.findViewById(R.id.q1);
                break;
            case 1:
                current_score = (TextView)v.findViewById(R.id.q2);
                break;
            case 2:
                current_score = (TextView)v.findViewById(R.id.q3);
                break;
            case 3:
                current_score = (TextView)v.findViewById(R.id.q4);
                break;
            case 4:
                current_score = (TextView)v.findViewById(R.id.q5);
                break;
            case 5:
                current_score = (TextView)v.findViewById(R.id.q6);
                break;
            case 6:
                current_score = (TextView)v.findViewById(R.id.q7);
                break;
            case 7:
                current_score = (TextView)v.findViewById(R.id.q8);
                break;
            case 8:
                current_score = (TextView)v.findViewById(R.id.q9);
                break;
            case 9:
                current_score = (TextView)v.findViewById(R.id.q10);
                break;
            case 10:
                current_score = (TextView)v.findViewById(R.id.q11);
                break;
            case 11:
                current_score = (TextView)v.findViewById(R.id.q12);
                break;
            case 12:
                current_score = (TextView)v.findViewById(R.id.q13);
                break;
            default:
                break;
        }
        myAnimate();
        return v;
    }


}
