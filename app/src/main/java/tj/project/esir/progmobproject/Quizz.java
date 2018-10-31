package tj.project.esir.progmobproject;
import org.json.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    LinearLayout multipleAnswersLayout;
    LinearLayout calculAnswerLayout;
    EditText calculAnswerInput;
    Button btn_valid_calcul;
    int resultatCalcul;
    int reponseValidee;
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
        multipleAnswersLayout = findViewById(R.id.multipleAnswersLayout);
        calculAnswerLayout = findViewById(R.id.calculAnswerLayout);
        calculAnswerInput = findViewById(R.id.calculAnswerInput);
        btn_valid_calcul = findViewById(R.id.validCalculButton);


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
                calculAnswerInput.setText("");
                pickQuestion();
            }
        });
        btn_valid_calcul.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String result = calculAnswerInput.getText().toString();
                if(result.equals(""))
                    result = "-1";
                setReponseTextQuizz(resultatCalcul == Integer.parseInt(result));
                closeKeyboard();
            }
        });
        pickQuestion();
    }

    public void pickQuestion() {
        reponseValidee = -1;
        setReponseTextQuizz("");
        Random rand = new Random();
        int typeQuestion = rand.nextInt(2);
        if (typeQuestion == 0){
            calculAnswerLayout.setVisibility(View.INVISIBLE);
            multipleAnswersLayout.setVisibility(View.VISIBLE);
            JSONObject choix;
            int indexQuestion = rand.nextInt(jsonArray.length());
            try {
                choix = (JSONObject) jsonArray.get(indexQuestion);
                JSONArray reponses = choix.getJSONArray("reponses");
                title_question.setText((String) choix.get("question"));
                btn_rep1.setText((String) ((JSONObject) reponses.get(0)).get("intitule"));
                btn_rep2.setText((String) ((JSONObject) reponses.get(1)).get("intitule"));
                btn_rep3.setText((String) ((JSONObject) reponses.get(2)).get("intitule"));
                rep1 = (Boolean) ((JSONObject) reponses.get(0)).get("valeur");
                rep2 = (Boolean) ((JSONObject) reponses.get(1)).get("valeur");
                rep3 = (Boolean) ((JSONObject) reponses.get(2)).get("valeur");
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            calculAnswerLayout.setVisibility(View.VISIBLE);
            multipleAnswersLayout.setVisibility(View.INVISIBLE);
            int variable1 = rand.nextInt(9)+1;
            int variable2 = rand.nextInt(9)+1;
            resultatCalcul = variable1*variable2;
            title_question.setText(variable1+" x "+variable2);
        }
    }
    public void setReponseTextQuizz(boolean valeurRep){
        if(reponseValidee == -1) {
            if (valeurRep) {
                btn_nextQuestion.setBackground(getDrawable(R.drawable.quizz_button_shape_true));
                reponseValidee = 1; //true
            } else {
                btn_nextQuestion.setBackground(getDrawable(R.drawable.quizz_button_shape_false));
                reponseValidee = 0; //false
            }
            btn_nextQuestion.setText(R.string.next_question);
            btn_nextQuestion.setVisibility(View.VISIBLE);
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
