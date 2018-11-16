package tj.project.esir.progmobproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Random;

import tj.project.esir.progmobproject.ball_games.Balls;
import tj.project.esir.progmobproject.ball_games.Block;
import tj.project.esir.progmobproject.ball_games.MenuParam;

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
                Random rand1 = new Random();
                int startRange = 0, endRange = 2;
                int offsetValue =  endRange - startRange + 1;
                int  baseValue = (int)  (offsetValue * rand1.nextDouble());
                int r =  baseValue + startRange;
                System.out.println("Randrom : " + r);

                if(r == 0){
                    Intent quizz = new Intent(getApplicationContext(), QuizzActivity.class);
                    startActivity(quizz);
                }
                if(r == 1){
                    Intent compass = new Intent(getApplicationContext(), CompassActivity.class);
                    startActivity(compass);
                }
                else{
                    Intent ball = new Intent(getApplicationContext(), MenuParam.class);
                    startActivity(ball);

                }
                questionManager.close();
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
                            q.setResponse1(new Pair(jsonobject.getString(TEXT_RESPONSE1),jsonobject.getBoolean(VALUE_RESPONSE1) == true ? 1 : 0));
                            q.setResponse2(new Pair(jsonobject.getString(TEXT_RESPONSE2),jsonobject.getBoolean(VALUE_RESPONSE2) == true ? 1 : 0));
                            q.setResponse3(new Pair(jsonobject.getString(TEXT_RESPONSE3),jsonobject.getBoolean(VALUE_RESPONSE3) == true ? 1 : 0));
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
}
