package tj.project.esir.progmobproject.ball_games;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import tj.project.esir.progmobproject.R;

public class MenuParam extends AppCompatActivity {

    private Button btnSimple;
    private Button btnMoyen;
    private Button btnDifficult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_param_ball);

        btnSimple = findViewById(R.id.btn_simple);
        btnMoyen = findViewById(R.id.btn_moyen);
        btnDifficult = findViewById(R.id.btn_difficult);

        btnSimple.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                ball.putExtra("level", 1);
                startActivity(ball);
                finish();
            }
        });

        btnMoyen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                ball.putExtra("level", 2);
                startActivity(ball);
                finish();
            }
        });

        btnDifficult.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ball = new Intent(getApplicationContext(), Balls.class);
                ball.putExtra("level", 3);
                startActivity(ball);
                finish();
            }
        });
    }



}
