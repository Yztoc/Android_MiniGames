package tj.project.esir.progmobproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import tj.project.esir.progmobproject.ball_games.Balls;
import tj.project.esir.progmobproject.ball_games.MenuParam;

public class TutorialActivity extends AppCompatActivity {

    private int game = 1;
    private Button btnGame;
    Intent gameIntent = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        btnGame = findViewById(R.id.launch_game);

        btnGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(game ==1) gameIntent = new Intent(getApplicationContext(), MenuParam.class);
                if(game ==2) gameIntent = new Intent(getApplicationContext(), CompassActivity.class);
                if(game ==3) gameIntent = new Intent(getApplicationContext(), QuizzActivity.class);

                startActivity(gameIntent);
                overridePendingTransition(R.anim.slide, R.anim.slide_out);
                finish();
            }
        });

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();


        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.game1:
                if (checked) game = 1;
                break;
            case R.id.game2:
                if (checked) game = 2;
                break;
            case R.id.game3:
                if (checked) game = 3;
                break;
        }
    }


    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }
}