package tj.project.esir.progmobproject.multiplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

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
import tj.project.esir.progmobproject.models.CustomPair;
import tj.project.esir.progmobproject.models.Question;

public class MultiplayerActivity extends AppCompatActivity implements ChannelListener {

    QuestionManager questionManager;
    String connectionType;

    LinearLayout message_send_layout;
    Button btnDiscover;
    Button btnSend;
    ListView listView;
    TextView read_msg_box;
    TextView connectionStatus;
    EditText writeMsg;

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
    private boolean retryChannel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        questionManager = new QuestionManager(getApplicationContext());
        connectionType ="none";

        btnDiscover = findViewById(R.id.discover);
        btnSend = findViewById(R.id.sendButton);
        listView = findViewById(R.id.peerListView);
        read_msg_box = findViewById(R.id.readMsg);
        connectionStatus = findViewById(R.id.connectionStatus);
        writeMsg = findViewById(R.id.writeMsg);
        message_send_layout = findViewById(R.id.message_send_layout);
        message_send_layout.setVisibility(View.INVISIBLE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        exqListener();
        wifiManager.setWifiEnabled(true);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);

        mReceiver = new MultiplayerBroadcastReceiver(mManager,mChannel,this);
        mIntentFilter = new IntentFilter();

        // Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        disconnectFromPeer();
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
        message_send_layout.setVisibility(View.INVISIBLE);
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
            switch (msg.what){
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    ObjectInputStream in = null;
                    try {
                        in = new ObjectInputStream(new ByteArrayInputStream(readBuff));
                        multiplayer = (MultiplayParameters) in.readObject();
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
/*
                    String tempMsg = new String(readBuff,0,msg.arg1);
                    try {
                        JSONObject receivedMsg = new JSONObject(tempMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    */
                    read_msg_box.setText(multiplayer.toString());
                    Intent balls = new Intent(getApplicationContext(), Balls.class);
                    balls.putExtra("multiplayer",  multiplayer);
                    startActivity(balls);
                    overridePendingTransition(R.anim.slide,R.anim.slide_out);

                    break;
            }
            return true;
        }
    });



    WifiP2pManager.PeerListListener peerListListener = new  WifiP2pManager.PeerListListener(){
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index =0;

                for( WifiP2pDevice device : peerList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index ++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,deviceNameArray);
                listView.setAdapter(adapter);
            }
            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(),"No Device Found",Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener(){

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAdress = info.groupOwnerAddress;
            if(info.groupFormed && info.isGroupOwner){
                connectionStatus.setText("Host");
                serverClass = new ServerClass();
                serverClass.start();
                connectionType = "server";
                message_send_layout.setVisibility(View.VISIBLE);
            }
            else if (info.groupFormed){
                connectionStatus.setText("Client");
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
        disconnectFromPeer();
    }

    private void exqListener() {
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener(){

                    @Override
                    public void onSuccess() {
                       connectionStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText("Discovery Started Failed");
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
                        Toast.makeText(getApplicationContext(),"Connected to "+device.deviceName,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionManager.open();
                List<Question> lQuestion = questionManager.get5randomId(); // recupération de 10 id de questions avant de les envoyer à l'autre device
                List<CustomPair<Integer,Integer>> lCalculs = get5mentalCalculs();
                MultiplayParameters multiplayParameters = new MultiplayParameters(1,lQuestion,lCalculs);
                questionManager.close();
                // String msg = writeMsg.getText().toString();
                if(sendReceive != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(bos);
                        oos.writeObject(multiplayParameters);
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] data = bos.toByteArray();
                    sendReceive.write(data);
                }
                Intent balls = new Intent(getApplicationContext(), Balls.class);
                balls.putExtra("multiplayer",  multiplayParameters);
                startActivity(balls);
                overridePendingTransition(R.anim.slide,R.anim.slide_out);

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

            while (socket != null) {
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
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        mManager.removeGroup(mChannel, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        disconnectFromPeer();
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

    public void disconnectFromPeer(){
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
    }
}


