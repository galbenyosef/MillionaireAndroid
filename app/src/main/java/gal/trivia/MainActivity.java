package gal.trivia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    RadioGroup radioTypeGroup;
    Button start;
    String GameType,GameCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GameType=null;
        GameCategory=null;
        setSpinner();
        setRadioGroup();
        setStartBtn();
        Audio.playSound(this,R.raw.open);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Audio.playSound(this,R.raw.open);
    }

    void setStartBtn(){
        start=(Button)findViewById(R.id.btn_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameCategory = GameCategory==null? "General": GameCategory;
                if (GameType!=null){
                    startGame();
                }
            }
        });
    }

    void setRadioGroup(){

        radioTypeGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.btn_mutliple) {
                    GameType = "multiple";
                } else {
                    GameType = "boolean";
                }

            }
        });
    }

    void setSpinner(){
        final List<String> categories = new ArrayList<String>();

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                GameCategory = categories.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        categories.add("General");
        categories.add("Sports");
        categories.add("Computers");
        categories.add("Mathematics");
        categories.add("Mythology");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
    }

    void startGame(){
        Intent game = new Intent(MainActivity.this,GameActivity.class);
        game.putExtra("type",GameType);
        game.putExtra("category",GameCategory);
        startActivity(game);
    }
}
