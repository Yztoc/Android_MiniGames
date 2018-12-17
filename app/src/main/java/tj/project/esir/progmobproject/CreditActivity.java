package tj.project.esir.progmobproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import tj.project.esir.progmobproject.db.ScoreManager;
import tj.project.esir.progmobproject.models.Score;


public class CreditActivity extends AppCompatActivity {

    private Button btnBack;
    private ScoreManager scoreManger;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        scoreManger = new ScoreManager(this);
        scoreManger.open();

        ArrayList<Score> tabScore = scoreManger.getAllScore();
        scoreManger.close();

        TextView textViewScoreBall = (TextView)findViewById(R.id.scoreBall);
        TextView textViewScoreCompass = (TextView)findViewById(R.id.scoreCompass);
        TextView textViewScoreQuizz = (TextView)findViewById(R.id.scoreQuizz);
        TextView textViewScoreFinal = (TextView)findViewById(R.id.scoreFinal);
        textViewScoreBall.setText("Score " + tabScore.get(0).getName_game() +  " : " + tabScore.get(0).getScore());
        textViewScoreCompass.setText("Score " + tabScore.get(1).getName_game() +  " : " + tabScore.get(1).getScore());
        textViewScoreQuizz.setText("Score " + tabScore.get(2).getName_game() +  " : " + tabScore.get(2).getScore());
        textViewScoreFinal.setText("Score "+ tabScore.get(3).getName_game() +  " : " + tabScore.get(3).getScore());



        Switch switchSong = findViewById(R.id.switchActiveSong);
        switchSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("PREFS", MODE_PRIVATE);
                if(isChecked){
                    sharedPreferences.edit()
                            .putInt("activeSong", 1)
                            .apply();
                }else{
                    sharedPreferences.edit()
                            .putInt("activeSong", 0)
                            .apply();
                }
                System.out.println("PARAMETER  :  " + sharedPreferences.getInt("activeSong", 0));
            }
        });

        btnBack = findViewById(R.id.btn_backmenu_credit);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                // start the new activity
                startActivity(menu);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);

                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }


}
