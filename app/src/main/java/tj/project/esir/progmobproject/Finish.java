package tj.project.esir.progmobproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import tj.project.esir.progmobproject.db.ScoreManager;
import tj.project.esir.progmobproject.models.Score;

public class Finish extends AppCompatActivity {


    private Score scoreBall;
    private  Score scoreCompass;
    private Score scoreQuizz;
    private Score scoreTotal;

    private Button back;

    private ScoreManager scoreManger;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_finish);

        scoreManger = new ScoreManager(this);
        scoreManger.open();

        // recoit le score de l'activity précédente
        Intent iin= getIntent();
        Bundle q = iin.getExtras();

        if(q!=null){
            scoreBall = (Score) q.get("scoreBall");
            scoreCompass = (Score) q.get("scoreCompass");
            scoreQuizz = (Score) q.get("scoreQuizz");
        }

        scoreTotal = new Score(4,"Final",scoreBall.getScore() + scoreCompass.getScore() + scoreQuizz.getScore());
        saveScore();

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        if(sharedPreferences.getInt("activeSong", 0) == 1){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.finish);;
            mp.start();
        }


        TextView textViewScoreBall = (TextView)findViewById(R.id.scoreBall);
        TextView textViewScoreCompass = (TextView)findViewById(R.id.scoreCompass);
        TextView textViewScoreQuizz = (TextView)findViewById(R.id.scoreQuizz);
        TextView textViewScoreFinal = (TextView)findViewById(R.id.scoreFinal);
        textViewScoreBall.setText("Score jeux balle : " + scoreBall.getScore());
        textViewScoreCompass.setText("Score jeux coffre fort : " + scoreCompass.getScore());
        textViewScoreQuizz.setText("Score quizz : " + scoreQuizz.getScore());
        textViewScoreFinal.setText("Score Final : " + scoreTotal.getScore());

        back = findViewById(R.id.btn_backmenu);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                // start the new activity
                startActivity(menu);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                scoreManger.close();
                finish();
            }
        });

    }

    public void  saveScore(){

        ArrayList<Score> tabScore = scoreManger.getAllScore();
        if(tabScore.size() == 0){
            scoreManger.addScore(new Score(1,"Ball games",scoreBall.getScore()));
            scoreManger.addScore(new Score(2,"Compass Game",scoreCompass.getScore()));
            scoreManger.addScore(new Score(3,"Quizz Game",scoreQuizz.getScore()));
            scoreManger.addScore(new Score(4,"Final",scoreTotal.getScore()));
        }else{
            for (Score elem : tabScore) {
                switch (elem.getId()){
                    case 1 :
                        if(elem.getScore() < scoreBall.getScore()) scoreManger.updateScore(scoreBall);
                        break;
                    case 2 :
                        if(elem.getScore() < scoreCompass.getScore()) scoreManger.updateScore(scoreCompass);
                        break;
                    case 3 :
                        if(elem.getScore() < scoreQuizz.getScore()) scoreManger.updateScore(scoreQuizz);
                        break;
                    case 4 :
                        if(elem.getScore() < scoreTotal.getScore()) scoreManger.updateScore(scoreTotal);
                        break;
                }
            }
        }



    }

}