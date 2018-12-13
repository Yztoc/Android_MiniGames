package tj.project.esir.progmobproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import tj.project.esir.progmobproject.ball_games.Balls;
import tj.project.esir.progmobproject.ball_games.MenuParam;

public class MenuSinglePlayer extends AppCompatActivity {

    private Button btnTuto;
    private Button btnGame;
    private static String KEY_VALUE_LEVEL = "level";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_single_player);

        btnTuto = findViewById(R.id.btn_tuto);
        btnGame = findViewById(R.id.btn_game);

        btnTuto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent tuto = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(tuto);
                overridePendingTransition(R.anim.slide, R.anim.slide_out);
                finish();
            }
        });

        btnGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), MenuParam.class);
                startActivity(ball);
                overridePendingTransition(R.anim.slide, R.anim.slide_out);
            }
        });

    }

    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }
}