package tj.project.esir.progmobproject;
import org.json.*;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.util.Random;

public class Quizz extends AppCompatActivity {

    String questions;
    Button btn_rep1;
    Button btn_rep2;
    Button btn_rep3;
    Button btn_nextQuestion;
    TextView title_question;
    JSONArray jsonArray;
    Boolean rep1;
    Boolean rep2;
    Boolean rep3;
    TextView reponseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        try {
            // récupération du contenu du fichier questions.json
            InputStream is = getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            questions = new String(buffer);
            // création d'un tableau, format JSON
            jsonArray = new JSONArray(questions);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        reponseText = findViewById(R.id.reponseText);
        btn_rep1 = findViewById(R.id.btn_rep1);
        btn_rep2 = findViewById(R.id.btn_rep2);
        btn_rep3 = findViewById(R.id.btn_rep3);
        title_question = findViewById(R.id.quizz_question);
        btn_nextQuestion =findViewById(R.id.btn_next_question);

        pickQuestion();


        btn_rep1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setReponseTextQuizz(rep1);
            }
        });
        btn_rep2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setReponseTextQuizz(rep2);
            }
        });
        btn_rep3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setReponseTextQuizz(rep3);
            }
        });

        btn_nextQuestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pickQuestion();
            }
        });

    }

    public void pickQuestion(){
        setReponseTextQuizz("");
        Random rand = new Random();
        JSONObject choix;
        int indexQuestion = rand.nextInt(jsonArray.length());
        try {
            choix = (JSONObject) jsonArray.get(indexQuestion);
            JSONArray reponses = choix.getJSONArray("reponses");
            title_question.setText((String)choix.get("question"));
            btn_rep1.setText((String)((JSONObject)reponses.get(0)).get("intitule"));
            btn_rep2.setText((String)((JSONObject)reponses.get(1)).get("intitule"));
            btn_rep3.setText((String)((JSONObject)reponses.get(2)).get("intitule"));
            rep1 = (Boolean)((JSONObject)reponses.get(0)).get("valeur");
            rep2 = (Boolean)((JSONObject)reponses.get(1)).get("valeur");
            rep3 = (Boolean)((JSONObject)reponses.get(2)).get("valeur");
        }
        catch (org.json.JSONException e){
            e.printStackTrace();
        }
    }
    public void setReponseTextQuizz(boolean valeurRep){
        if(valeurRep){
            reponseText.setText(R.string.great_response_quizz);
            btn_nextQuestion.setText(R.string.next_question);
            btn_nextQuestion.setVisibility(View.VISIBLE);
        }
        else {
            reponseText.setText(R.string.bad_response_quizz);
            btn_nextQuestion.setVisibility(View.INVISIBLE);
        }
    }
    public void setReponseTextQuizz(String s) {
        if (s.isEmpty()) {
            reponseText.setText("");
            btn_nextQuestion.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(home);
        finish();
    }
}
