package tj.project.esir.progmobproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class CreditActivity extends AppCompatActivity {

    private Button btnBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        btnBack = findViewById(R.id.btn_difficult);
        btnBack.setOnClickListener(new View.OnClickListener() {
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
