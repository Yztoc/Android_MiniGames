package tj.project.esir.progmobproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tj.project.esir.progmobproject.ball_games.Balls;

public class Finish extends AppCompatActivity {


    private int scoreBall = 0;
    private  int scoreCompass = 0;
    private int scoreQuizz = 0;
    private int scoreTotal = 0;

    private Button back;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_finish);

        // recoit le score de l'activity précédente
        Intent iin= getIntent();
        Bundle q = iin.getExtras();

        if(q!=null){
            scoreBall = (int) q.get("scoreBall");
            scoreCompass = (int) q.get("scoreCompass");
            scoreQuizz = (int) q.get("scoreQuizz");
        }


        scoreTotal = scoreBall + scoreCompass + scoreQuizz;

        TextView textViewScoreBall = (TextView)findViewById(R.id.scoreBall);
        TextView textViewScoreCompass = (TextView)findViewById(R.id.scoreCompass);
        TextView textViewScoreQuizz = (TextView)findViewById(R.id.scoreQuizz);
        TextView textViewScoreFinal = (TextView)findViewById(R.id.scoreFinal);
        textViewScoreBall.setText("Score jeux balle : " + scoreBall);
        textViewScoreCompass.setText("Score jeux coffre fort : " + scoreCompass);
        textViewScoreQuizz.setText("Score quizz : " + scoreQuizz);
        textViewScoreFinal.setText("SCORE FINAL : " + scoreQuizz);

        back = findViewById(R.id.btn_backmenu);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent menu = new Intent(getApplicationContext(), MainActivity.class);
                // start the new activity
                startActivity(menu);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                finish();
            }
        });


    }

}