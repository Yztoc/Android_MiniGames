package tj.project.esir.progmobproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tj.project.esir.progmobproject.ball_games.Balls;
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
               // Intent quizz = new Intent(getApplicationContext(), Quizz.class);
               // startActivity(quizz);
               //Intent ball = new Intent(getApplicationContext(), MenuParam.class);
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                startActivity(ball);
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
