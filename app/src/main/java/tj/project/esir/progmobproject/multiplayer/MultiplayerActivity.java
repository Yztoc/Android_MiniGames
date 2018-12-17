package tj.project.esir.progmobproject.multiplayer;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tj.project.esir.progmobproject.MainActivity;
import tj.project.esir.progmobproject.R;
import tj.project.esir.progmobproject.ball_games.Balls;
import tj.project.esir.progmobproject.db.QuestionManager;
import tj.project.esir.progmobproject.db.ScoreManager;
import tj.project.esir.progmobproject.models.CustomPair;
import tj.project.esir.progmobproject.models.Question;
import tj.project.esir.progmobproject.models.Score;

import static tj.project.esir.progmobproject.R.*;

public class MultiplayerActivity extends AppCompatActivity implements ChannelListener {

    Activity main;

    QuestionManager questionManager;
    String connectionType;

    RadioGroup radioGroup;
    Button sendButton;
    TextView levelRadioGroup;
    Button btnDiscover;
    Button btnSend;
    ListView listView;
    TextView connectionStatus;

    WifiP2pManager mManager;
    Channel mChannel;
    BroadcastReceiver mReceiver;

    IntentFilter mIntentFilter;
    WifiManager wifiManager;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ = 1;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;
    private int level = 1;
    private boolean retryChannel = false;

    private Score scoreBall;
    private  Score scoreCompass;
    private Score scoreQuizz;
    private Score scoreTotal;

    private Button back;

    private ScoreManager scoreManger;
    private MultiplayParameters multi;

    private TextView resultatStatus;
    private String resultatMultiplayer;
    boolean multiplayerFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = this;
        Intent iin= getIntent();
        Bundle q = iin.getExtras();


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiManager.setWifiEnabled(true);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mReceiver = new MultiplayerBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();

        // Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if(q!=null){
            setContentView(layout.activity_finish_multiplayer);
            resultatMultiplayer ="";
            scoreBall = (Score) q.get("scoreBall");
            scoreCompass = (Score) q.get("scoreCompass");
            scoreQuizz = (Score) q.get("scoreQuizz");
            scoreManger = new ScoreManager(this);
            scoreManger.open();
            multi =  (MultiplayParameters) q.get("multiplayer");
            connectionType = multi.getConnectionType();
            System.out.println("connectionType "+connectionType);

            scoreTotal = new Score(4,"Final",scoreBall.getScore() + scoreCompass.getScore() + scoreQuizz.getScore());

            TextView textViewScoreBall = (TextView)findViewById(id.scoreBall);
            TextView textViewScoreCompass = (TextView)findViewById(id.scoreCompass);
            TextView textViewScoreQuizz = (TextView)findViewById(id.scoreQuizz);
            TextView textViewScoreFinal = (TextView)findViewById(id.scoreFinal);
            textViewScoreBall.setText("Score jeux balle : " + scoreBall.getScore());
            textViewScoreCompass.setText("Score jeux coffre fort : " + scoreCompass.getScore());
            textViewScoreQuizz.setText("Score quizz : " + scoreQuizz.getScore());
            textViewScoreFinal.setText("Score Final : " + scoreTotal.getScore());

            resultatStatus = findViewById(id.resultatStatus);
            resultatStatus.setText(string.waitingResultat);
            multiplayerFinish = true;

            back = findViewById(id.btn_backmenuMulti);

            back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent multiplayerActivity = new Intent(getApplicationContext(), MultiplayerActivity.class);
                    // start the new activity
                    mManager.removeGroup(mChannel, new ActionListener() {
                        @Override
                        public void onSuccess() {
                            disconnectFromPeer("");
                        }
                        @Override
                        public void onFailure(int reason) {
                        }
                    });
                    startActivity(multiplayerActivity);
                    overridePendingTransition(anim.slide, anim.slide_out);
                    scoreManger.close();
                    finish();
                }
            });

        }
        else {
            setContentView(layout.activity_multiplayer);

            multiplayerFinish = false;
            questionManager = new QuestionManager(getApplicationContext());
            connectionType = "none";

            btnDiscover = findViewById(id.discover);
            btnSend = findViewById(id.sendButton);
            listView = findViewById(id.peerListView);
            connectionStatus = findViewById(id.connectionStatus);
            radioGroup = findViewById(R.id.radioGroup);
            sendButton = findViewById(id.sendButton);
            levelRadioGroup = findViewById(R.id.levelRadioGroup);
            sendButton.setVisibility(View.INVISIBLE);
            levelRadioGroup.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            exqListener();
        }
    }


    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
        sendButton.setVisibility(View.INVISIBLE);
        levelRadioGroup.setVisibility(View.INVISIBLE);
        radioGroup.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getWifi();
        }
    }

    private void getWifi() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            MultiplayParameters multiplayer = new MultiplayParameters();
            SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("PREF_NAME", MODE_PRIVATE);
            switch (msg.what){
                case MESSAGE_READ:

                    byte[] readBuff = (byte[]) msg.obj;
                    ByteArrayInputStream in = null;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    if(!multiplayerFinish) {
                        try {
                            JSONArray receivedMsg = new JSONArray(tempMsg);
                            multiplayer.setLevel(receivedMsg.getInt(0));
                            JSONArray questionsIDs = receivedMsg.getJSONArray(1);
                            questionManager.open();
                            for (int i = 0; i < questionsIDs.length(); i++) {
                                Question q = questionManager.getQuestion(questionsIDs.getInt(i));
                                multiplayer.addQuestion(questionManager.getQuestion(questionsIDs.getInt(i)));
                            }
                            JSONArray calculs = receivedMsg.getJSONArray(2);
                            for (int i = 0; i < calculs.length(); i++) {
                                JSONObject temp = calculs.getJSONObject(i);
                                CustomPair<Integer, Integer> tempPair = new CustomPair<>(temp.getInt("variable1"), temp.getInt("variable2"));
                                multiplayer.addCalcul(tempPair);
                            }
                            multiplayer.setConnectionType(connectionType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        questionManager.close();
                        Intent balls = new Intent(getApplicationContext(), Balls.class);
                        balls.putExtra("multiplayer", multiplayer);
                        startActivity(balls);
                        overridePendingTransition(anim.slide, anim.slide_out);
                    }
                    else{
                        if(tempMsg.equals("defaite") || tempMsg.equals("victoire") || tempMsg.equals("egalite")){
                            MediaPlayer mp = null;
                            if(tempMsg.equals("defaite")){
                                resultatStatus.setText(string.inferiorResultat);
                                resultatMultiplayer = "defaite";
                                 mp = MediaPlayer.create(main, raw.loose);
                            }
                            else if(tempMsg.equals("victoire")){
                                resultatStatus.setText(string.superiorResultat);
                                resultatMultiplayer = "victoire";
                                mp = MediaPlayer.create(main, raw.win);

                            }
                            else {
                                resultatStatus.setText(string.equalResultat);
                                resultatMultiplayer = "egalite";
                               // mp = MediaPlayer.create(main, raw.win);
                            }

                            if(sharedPreferences.getInt("activeSong", 0) == 1){
                                mp.start();
                            }

                            disconnectFromPeer("");
                        }
                        else {
                            MediaPlayer mp = null;
                            int resultatAdversaire = Integer.valueOf(tempMsg);
                            String resultatAdversaireAEnvoyer = "defaite";
                            if (resultatAdversaire > scoreTotal.getScore()) {
                                resultatStatus.setText(string.inferiorResultat);
                                resultatMultiplayer = "defaite";
                                resultatAdversaireAEnvoyer = "victoire";
                                mp = MediaPlayer.create(main, raw.loose);
                            } else if (resultatAdversaire == scoreTotal.getScore()) {
                                resultatStatus.setText(string.equalResultat);
                                resultatMultiplayer = "egalite";
                                resultatAdversaireAEnvoyer = resultatMultiplayer;
                               // mp = MediaPlayer.create(main, raw.win);

                            } else {
                                resultatStatus.setText(string.superiorResultat);
                                resultatMultiplayer = "victoire";
                                resultatAdversaireAEnvoyer = "defaite";
                                mp = MediaPlayer.create(main, raw.win);

                            }
                            sendReceive.write(resultatAdversaireAEnvoyer.getBytes());
                            if(sharedPreferences.getInt("activeSong", 0) == 1){
                                mp.start();
                            }
                        }
                    }
                    break;
            }
            return true;
        }
    });



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case id.easy:
                if (checked) level = 1;
                break;
            case id.moyen:
                if (checked) level = 2;
                break;
            case id.hard:
                if (checked)level = 3;
                break;
        }
    }

    WifiP2pManager.PeerListListener peerListListener = new  WifiP2pManager.PeerListListener(){
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!multiplayerFinish) {
                if (!peerList.getDeviceList().equals(peers)) {
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());

                    deviceNameArray = new String[peerList.getDeviceList().size()];
                    deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                    int index = 0;

                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        deviceNameArray[index] = device.deviceName;
                        deviceArray[index] = device;
                        index++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                    listView.setAdapter(adapter);
                }
                if (peers.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Aucun joueur trouvé", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    };
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener(){

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAdress = info.groupOwnerAddress;
            if(info.groupFormed && info.isGroupOwner){
                serverClass = new ServerClass();
                serverClass.start();
                connectionType = "server";
                if(!multiplayerFinish) {
                    connectionStatus.setText("Server");
                    sendButton.setVisibility(View.VISIBLE);
                    levelRadioGroup.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                }
            }
            else if (info.groupFormed){
                if(!multiplayerFinish) {
                    connectionStatus.setText("Client");
                }
                clientClass = new ClientClass(groupOwnerAdress);
                clientClass.start();
                connectionType = "client";
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new MultiplayerBroadcastReceiver(mManager,mChannel,this);
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        if(multiplayerFinish)
            disconnectFromPeer("quit");
        else
            disconnectFromPeer("");
    }

    private void exqListener() {
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener(){

                    @Override
                    public void onSuccess() {
                        connectionStatus.setText(string.discoverOn);
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText(R.string.discoverPb);
                    }
                });
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                mManager.connect(mChannel,config,new WifiP2pManager.ActionListener(){

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connecté à"+device.deviceName,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(),"Non connecté",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionManager.open();
                List<Question> lQuestion = questionManager.get5randomQuestions(); // recupération de 10 id de questions avant de les envoyer à l'autre device
                List<CustomPair<Integer,Integer>> lCalculs = get5mentalCalculs();
                MultiplayParameters multiplayParameters = new MultiplayParameters(level,lQuestion,lCalculs,connectionType);
                questionManager.close();
                if(sendReceive != null) {
                    JSONArray msg = new JSONArray();
                    msg.put(level);
                    JSONArray questionsIDs = new JSONArray();
                    for(Question q : lQuestion){
                        questionsIDs.put(q.getId());
                    }
                    msg.put(questionsIDs);
                    JSONArray calculs = new JSONArray();
                    for(CustomPair c : lCalculs){
                        JSONObject temp = new JSONObject();
                        try {
                            temp.put("variable1",c.getFirst());
                            temp.put("variable2",c.getSecond());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        calculs.put(temp);
                    }
                    msg.put(calculs);
                    sendReceive.write(msg.toString().getBytes());
                }

                Intent balls = new Intent(getApplicationContext(), Balls.class);
                balls.putExtra("multiplayer",  multiplayParameters);
                startActivity(balls);
                overridePendingTransition(anim.slide, anim.slide_out);

            }
        });
    }


    public class ServerClass extends Thread{
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run(){
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket socket) {
            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null && !socket.isClosed()) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(final byte[] bytes) {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAdress){
            hostAdd = hostAdress.getHostAddress();
            socket = new Socket();
        }
        public void run(){

            if(multiplayerFinish){
                boolean notCo = true;
                while(notCo){
                    try {
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                        notCo = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                try {
                    socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sendReceive = new SendReceive(socket);
            sendReceive.start();
            if(multiplayerFinish){
                while(resultatMultiplayer.equals("")) {
                    sendReceive.write(String.valueOf(scoreTotal.getScore()).getBytes());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mManager.removeGroup(mChannel, new ActionListener() {
            @Override
            public void onSuccess() {
                disconnectFromPeer("");
            }
            @Override
            public void onFailure(int reason) {
            }
        });
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }

    public List<CustomPair<Integer,Integer>> get5mentalCalculs(){
        Random rand = new Random();
        List<CustomPair<Integer,Integer>> res = new ArrayList<CustomPair<Integer, Integer>>();
        int variable1;
        int variable2;
        for(int i =0; i < 5;i++) {
            variable1 = rand.nextInt(9) + 1;
            variable2 = rand.nextInt(9) + 1;
            res.add(new CustomPair<Integer, Integer>(variable1,variable2));
        }
        return res;
    }

    public void disconnectFromPeer(String state){
        if(connectionType.equals("server")){
            try {
                if(!serverClass.serverSocket.isClosed())
                    serverClass.serverSocket.close();
                if(!serverClass.socket.isClosed())
                    serverClass.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (connectionType.equals("client")){
            if(!clientClass.socket.isClosed()) {
                try {
                    clientClass.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!connectionType.equals("none") && !sendReceive.socket.isClosed()) {
            try {
                sendReceive.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectionType ="none";
        if (state.equals("changeP2PState") && multiplayerFinish) {
            resultatStatus.setText("aaa");
            resultatMultiplayer = "victoire";
        }
    }
}