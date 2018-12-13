package tj.project.esir.progmobproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import tj.project.esir.progmobproject.ball_games.MenuParam;

public class TutorialActivity extends AppCompatActivity {

    private int game = 1;
    private Button btnGame;
    Intent gameIntent = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.tutoRadioGroup);


        ImageView img1 = (ImageView) findViewById(R.id.img_tuto1);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = 1;
                RadioButton game1 = (RadioButton) findViewById(R.id.game1);
                game1.setChecked(true);
                game1.setSelected(true);
            }
        });

        ImageView img2 = (ImageView) findViewById(R.id.img_tuto2);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = 2;

                RadioButton game2 = (RadioButton) findViewById(R.id.game2);
                game2.setChecked(true);
                game2.setSelected(true);
            }
        });

        ImageView img3 = (ImageView) findViewById(R.id.img_tuto3);
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = 3;

                RadioButton game3 = (RadioButton) findViewById(R.id.game3);
                game3.setChecked(true);
                game3.setSelected(true);
            }
        });




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