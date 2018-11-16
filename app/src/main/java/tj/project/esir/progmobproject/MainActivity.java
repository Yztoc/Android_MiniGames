package tj.project.esir.progmobproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import tj.project.esir.progmobproject.ball_games.Balls;
import tj.project.esir.progmobproject.ball_games.Block;
import tj.project.esir.progmobproject.ball_games.MenuParam;

public class MainActivity extends AppCompatActivity {

    private Button btnSinglePlayer;
    private Button btnMultiplayer;
    private Button btnCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSinglePlayer = findViewById(R.id.btn_singleplayer);
        btnMultiplayer = findViewById(R.id.btn_multiplayer);
        btnCredit = findViewById(R.id.btn_credit);

        btnSinglePlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Random rand1 = new Random();
                int startRange = 0, endRange = 1;
                int offsetValue =  endRange - startRange + 1;
                int  baseValue = (int)  (offsetValue * rand1.nextDouble());
                int r =  baseValue + startRange;
                System.out.println("Randrom : " + r);

                if(r == 0){
                    Intent quizz = new Intent(getApplicationContext(), Quizz.class);
                    startActivity(quizz);
                }else{
                    Intent ball = new Intent(getApplicationContext(), MenuParam.class);
                    startActivity(ball);

                }

                finish();
            }
        });
        btnMultiplayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("button 2");
            }
        });
        btnCredit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("button 3");
            }
        });
    }
}
