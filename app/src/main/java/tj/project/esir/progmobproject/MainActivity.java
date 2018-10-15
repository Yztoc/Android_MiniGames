package tj.project.esir.progmobproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnSinglePlayer;
    private Button btnMultiplayer;
    private Button btnCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_main);

        btnSinglePlayer = findViewById(R.id.btn_singleplayer);
        btnMultiplayer = findViewById(R.id.btn_multiplayer);
        btnCredit = findViewById(R.id.btn_credit);

        btnSinglePlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent quizz = new Intent(getApplicationContext(), Quizz.class);
                startActivity(quizz);
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

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
