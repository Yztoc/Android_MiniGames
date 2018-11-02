package tj.project.esir.progmobproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tj.project.esir.progmobproject.db.MajDB;
import tj.project.esir.progmobproject.db.QuestionManager;
import tj.project.esir.progmobproject.models.Question;

import org.json.*;

import static tj.project.esir.progmobproject.db.QuestionManager.*;

public class MainActivity extends AppCompatActivity {

    private Button btnSinglePlayer;
    private Button btnMultiplayer;
    private Button btnCredit;

    QuestionManager questionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        questionManager = new QuestionManager(this);

        OkHttpClient client = new OkHttpClient();
        questionManager.open();
        String url1 =MajDB.serverURLNewQuestions+questionManager.getLastInsertedId();
        questionManager.close();
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
                System.out.println("reep" +myResponse);
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
                        questionManager.open();
                        questionManager.addQuestion(q);
                        questionManager.close();
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                }
            }
        });
        questionManager.open();
        String url2 = MajDB.serverURLDeletedQuestion+questionManager.getAllQuestionsIds();
        questionManager.close();
        request = new Request.Builder().url(url2).build();
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
                        questionManager.open();
                        for (int i = 0; i < arrayOfRes.length(); i++) {
                            questionManager.deleteQuestion(arrayOfRes.getInt(i));
                        }
                        questionManager.close();
                    }
                    catch (JSONException e){
                    }
                }
            }
        });


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
}
