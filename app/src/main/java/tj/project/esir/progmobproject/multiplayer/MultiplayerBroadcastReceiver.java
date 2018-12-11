package tj.project.esir.progmobproject.multiplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MultiplayerBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private  MultiplayerActivity mActivity;

    public MultiplayerBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MultiplayerActivity mActivity){
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                Toast.makeText(context,"Wifi is ON",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if(mManager != null){
               mManager.requestPeers(mChannel,mActivity.peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(mManager == null) return;

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel,mActivity.connectionInfoListener);
            }
            else {
                mActivity.connectionStatus.setText("Device Disconnected");
                mActivity.message_send_layout.setVisibility(View.INVISIBLE);
                if(mActivity.connectionType.equals("server")){
                    try {
                        if(!mActivity.serverClass.serverSocket.isClosed())
                            mActivity.serverClass.serverSocket.close();
                        if(!mActivity.serverClass.socket.isClosed())
                            mActivity.serverClass.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (mActivity.connectionType.equals("client")){
                    System.out.println("client");
                    if(!mActivity.clientClass.socket.isClosed()) {
                        try {
                            mActivity.clientClass.socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(!mActivity.connectionType.equals("none") && !mActivity.sendReceive.socket.isClosed()) {
                    try {
                        mActivity.sendReceive.socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mActivity.connectionType ="none";
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }


}
