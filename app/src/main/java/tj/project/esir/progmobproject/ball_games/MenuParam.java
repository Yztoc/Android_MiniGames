package tj.project.esir.progmobproject.ball_games;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import tj.project.esir.progmobproject.MainActivity;
import tj.project.esir.progmobproject.R;

public class MenuParam extends AppCompatActivity {

    private Button btnSimple;
    private Button btnMoyen;
    private Button btnDifficult;
    private static String KEY_VALUE_LEVEL = "level";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_param_ball);

        btnSimple = findViewById(R.id.btn_simple);
        btnMoyen = findViewById(R.id.btn_moyen);
        btnDifficult = findViewById(R.id.btn_difficult);

        btnSimple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                ball.putExtra(KEY_VALUE_LEVEL, 1);
                // start the new activity
                startActivity(ball);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                finish();
            }
        });

        btnMoyen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                ball.putExtra(KEY_VALUE_LEVEL, 2);
                startActivity(ball);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                finish();
            }
        });

        btnDifficult.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                ball.putExtra(KEY_VALUE_LEVEL, 3);
                startActivity(ball);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                finish();
            }
        });
    }
    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }


}
