package tj.project.esir.progmobproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tj.project.esir.progmobproject.ball_games.MenuParam;
import tj.project.esir.progmobproject.db.MajDB;
import tj.project.esir.progmobproject.db.QuestionManager;
import tj.project.esir.progmobproject.models.CustomPair;
import tj.project.esir.progmobproject.models.Question;
import tj.project.esir.progmobproject.multiplayer.MultiplayerActivity;

import static tj.project.esir.progmobproject.db.QuestionManager.TEXT_RESPONSE1;
import static tj.project.esir.progmobproject.db.QuestionManager.TEXT_RESPONSE2;
import static tj.project.esir.progmobproject.db.QuestionManager.TEXT_RESPONSE3;
import static tj.project.esir.progmobproject.db.QuestionManager.TITLE_QUESTION;
import static tj.project.esir.progmobproject.db.QuestionManager.VALUE_RESPONSE1;
import static tj.project.esir.progmobproject.db.QuestionManager.VALUE_RESPONSE2;
import static tj.project.esir.progmobproject.db.QuestionManager.VALUE_RESPONSE3;

public class MainActivity extends AppCompatActivity {

    private Button btnSinglePlayer;
    private Button btnMultiplayer;
    private Button btnCredit;

    private QuestionManager questionManager;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionManager = new QuestionManager(this);
        client = new OkHttpClient();
        questionManager.open();

        getNewQuestions(); // récupération des nouvelles questions du serveur
        deleteQuestions(); // suppression des questions supprimées depuis le serveur

        setContentView(R.layout.activity_main);

        btnSinglePlayer = findViewById(R.id.btn_singleplayer);
        btnMultiplayer = findViewById(R.id.btn_multiplayer);
        btnCredit = findViewById(R.id.btn_credit);

        btnSinglePlayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent singlePlayer = new Intent(getApplicationContext(), MenuSinglePlayer.class);
                startActivity(singlePlayer);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                questionManager.close();
                finish();
            }
        });
        btnMultiplayer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent multiplayer = new Intent(getApplicationContext(), MultiplayerActivity.class);
                questionManager.close();
                startActivity(multiplayer);
            }
        });

        btnCredit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent credit = new Intent(getApplicationContext(), CreditActivity.class);
                questionManager.close();
                startActivity(credit);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);
                finish();
            }
        });
    }


    public void getNewQuestions(){
        // Récupération des nouvelles questions à ajouter à la bdd sqlite et ajout dans celle-ci
        String url1 =MajDB.serverURLNewQuestions+questionManager.getLastInsertedId();
        Request request = new Request.Builder().url(url1).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String myResponse = response.body().string();
                    try {
                        JSONArray arrayOfRes = new JSONArray(myResponse);
                        for (int i = 0; i < arrayOfRes.length(); i++) {
                            JSONObject jsonobject = arrayOfRes.getJSONObject(i);
                            Question q = new Question();
                            q.setId(jsonobject.getInt("_id"));
                            q.setTitle(jsonobject.getString(TITLE_QUESTION));
                            q.setResponse1(new CustomPair<String,Integer>(jsonobject.getString(TEXT_RESPONSE1),jsonobject.getBoolean(VALUE_RESPONSE1) == true ? 1 : 0));
                            q.setResponse2(new CustomPair<String,Integer>(jsonobject.getString(TEXT_RESPONSE2),jsonobject.getBoolean(VALUE_RESPONSE2) == true ? 1 : 0));
                            q.setResponse3(new CustomPair<String,Integer>(jsonobject.getString(TEXT_RESPONSE3),jsonobject.getBoolean(VALUE_RESPONSE3) == true ? 1 : 0));
                            questionManager.addQuestion(q);
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void deleteQuestions(){
        // récupération des id des questions à supprimer de la bdd sqlite et suppression des questions
        String url2 = MajDB.serverURLDeletedQuestion+questionManager.getAllQuestionsIds();
        Request request = new Request.Builder().url(url2).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String myResponse = response.body().string();
                    try {
                        JSONArray arrayOfRes = new JSONArray(myResponse);
                        for (int i = 0; i < arrayOfRes.length(); i++) {
                            questionManager.deleteQuestion(arrayOfRes.getInt(i));
                        }
                    }
                    catch (JSONException e){
                        System.out.println(e);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //nothing
    }
}
