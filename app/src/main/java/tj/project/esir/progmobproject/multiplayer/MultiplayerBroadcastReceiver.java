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

import tj.project.esir.progmobproject.R;

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
                Toast.makeText(context,"Wifi ON",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,"Wifi OFF",Toast.LENGTH_SHORT).show();
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
                if(!mActivity.multiplayerFinish) {
                    mActivity.connectionStatus.setText(R.string.discoverOff);
                    mActivity.message_send_layout.setVisibility(View.INVISIBLE);
                }
                mActivity.disconnectFromPeer("");
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }


}
