package tj.project.esir.progmobproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import tj.project.esir.progmobproject.db.QuestionManager;
import tj.project.esir.progmobproject.models.CustomPair;
import tj.project.esir.progmobproject.models.Question;
import tj.project.esir.progmobproject.models.Score;
import tj.project.esir.progmobproject.multiplayer.MultiplayParameters;
import tj.project.esir.progmobproject.multiplayer.MultiplayerActivity;

public class QuizzActivity extends AppCompatActivity {

    private Button btn_rep1;
    private Button btn_rep2;
    private Button btn_rep3;
    private Button btn_nextQuestion;
    private TextView title_question;
    private Boolean rep1;
    private Boolean rep2;
    private Boolean rep3;
    private TextView reponseText;
    private LinearLayout multipleAnswersLayout;
    private LinearLayout calculAnswerLayout;
    private EditText calculAnswerInput;
    private Button btn_valid_calcul;
    private int resultatCalcul;
    private int reponseValidee;
    private QuestionManager m;

    private int nbQuestion = 10;
    private int score = 0;
    private Score scoreBall;
    private Score scoreCompass;
    private MultiplayParameters multi = null;
    private List<CustomPair<Integer,Integer>> listCalcul = null;
    private List<Question> listQuestion = null;
    private int i = 0;
    boolean isTuto=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);


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
                if(multi == null)pickQuestion();
                else pickQuestionMultiplayer();
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
        m = new QuestionManager(this);

        // recoit le score de l'activity précédente
        Intent iin= getIntent();
        Bundle q = iin.getExtras();

        if(q!=null){
            if(q.get("tuto") != null) isTuto = true;
            if(q.get("multiplayer") != null){
                multi =  (MultiplayParameters) q.get("multiplayer");
                listCalcul =  multi.getListCalculs();
                listQuestion = multi.getListQuestion();
                pickQuestionMultiplayer();
            }else{
                m.open();
                listQuestion = m.get5randomQuestions();
                m.close();
                pickQuestion();
            }
            scoreBall = (Score) q.get("scoreBall");
            scoreCompass = (Score) q.get("scoreCompass");
        }

    }

    public void pickQuestionMultiplayer(){
        nbQuestion--;
        if(nbQuestion ==0){
            dialogFinish();
        }else{
            reponseValidee = -1;
            setReponseTextQuizz("");
            if(nbQuestion % 2 ==0){
                calculAnswerLayout.setVisibility(View.INVISIBLE);
                multipleAnswersLayout.setVisibility(View.VISIBLE);
                Question question = listQuestion.get(i);
                title_question.setText(question.getTitle());
                btn_rep1.setText(question.getResponse1().getFirst());
                btn_rep2.setText(question.getResponse2().getFirst());
                btn_rep3.setText(question.getResponse3().getFirst());
                rep1 = question.getResponse1().getSecond() == 0 ?  false : true;
                rep2 = question.getResponse2().getSecond() == 0 ?  false : true;
                rep3 = question.getResponse3().getSecond() == 0 ?  false : true;
            }else{
                calculAnswerLayout.setVisibility(View.VISIBLE);
                multipleAnswersLayout.setVisibility(View.INVISIBLE);
                int variable1 = listCalcul.get(i).getFirst();
                int variable2 = listCalcul.get(i).getSecond();
                resultatCalcul = variable1*variable2;
                title_question.setText(variable1+" x "+variable2);
                i++;
            }
        }
    }

    public void pickQuestion() {

        nbQuestion--;
        if(nbQuestion ==0){
            dialogFinish();
        }else{
            reponseValidee = -1;
            setReponseTextQuizz("");
            if(nbQuestion % 2 ==0){
                calculAnswerLayout.setVisibility(View.INVISIBLE);
                multipleAnswersLayout.setVisibility(View.VISIBLE);

                Question question = listQuestion.get(i);
                title_question.setText(question.getTitle());
                btn_rep1.setText(question.getResponse1().getFirst());
                btn_rep2.setText(question.getResponse2().getFirst());
                btn_rep3.setText(question.getResponse3().getFirst());
                rep1 = question.getResponse1().getSecond() == 0 ?  false : true;
                rep2 = question.getResponse2().getSecond() == 0 ?  false : true;
                rep3 = question.getResponse3().getSecond() == 0 ?  false : true;
            }
            else{
                Random rand = new Random();
                calculAnswerLayout.setVisibility(View.VISIBLE);
                multipleAnswersLayout.setVisibility(View.INVISIBLE);
                int variable1 = rand.nextInt(9)+1;
                int variable2 = rand.nextInt(9)+1;
                resultatCalcul = variable1*variable2;
                title_question.setText(variable1+" x "+variable2);
                i++;
            }
        }

    }
    public void setReponseTextQuizz(boolean valeurRep){
        if(reponseValidee == -1) {
            if (valeurRep) {
                btn_nextQuestion.setBackground(getDrawable(R.drawable.quizz_button_shape_true));
                reponseValidee = 1; //true
                score++;
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

    void dialogFinish(){
        Runnable second_Task = new Runnable() {
            public void run() {
                QuizzActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                        final AlertDialog.Builder alert = new AlertDialog.Builder(QuizzActivity.this,R.style.ThemeDialogCustom);
                        alert.setTitle("Terminé ! ");
                        alert.setMessage("Vous avez fini avec les stats suivant : "
                                + "\nScore Final : " + score);
                        alert.setCancelable(false);

                        alert.setPositiveButton("Finir", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(isTuto){
                                        Intent tuto = new Intent(getApplicationContext(), TutorialActivity.class);
                                        startActivity(tuto);
                                        overridePendingTransition(R.anim.slide,R.anim.slide_out);
                                }
                                else {
                                    Intent finish = new Intent(getApplicationContext(), Finish.class);
                                    if (multi != null) {
                                        finish = new Intent(getApplicationContext(), MultiplayerActivity.class);
                                    }
                                    finish.putExtra("scoreBall", scoreBall);
                                    finish.putExtra("scoreCompass", scoreCompass);
                                    finish.putExtra("scoreQuizz", new Score(3, "Quizz Game", score));
                                    finish.putExtra("multiplayer", multi);


                                    startActivity(finish);
                                    overridePendingTransition(R.anim.slide, R.anim.slide_out);
                                }
                            }
                        });

                        alert.show();
                    }
                });

            }
        };
        second_Task.run();
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
