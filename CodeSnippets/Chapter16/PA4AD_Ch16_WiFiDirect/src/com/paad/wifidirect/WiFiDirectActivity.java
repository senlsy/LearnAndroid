package com.paad.wifidirect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class WiFiDirectActivity extends Activity
{
    
    private static final String TAG="WiFiDirectActivity";
    private ListView listView;
    private ArrayAdapter aa;
    private TextView tv;
    private Button buttonDiscover;
    IntentFilter peerfilter;
    IntentFilter connectionfilter;
    IntentFilter p2pEnabled;
    private Handler handler=new Handler();
    private WifiP2pManager wifiP2pManager;
    private Channel wifiDirectChannel;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv=(TextView)findViewById(R.id.textView);
        listView=(ListView)findViewById(R.id.listView);
        aa=new ArrayAdapter<WifiP2pDevice>(this, android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(aa);
        initializeWiFiDirect();
        peerfilter=new IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        connectionfilter=new IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        p2pEnabled=new IntentFilter(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);// ����wifi
                                                                                  // direct״̬�ı仯
        buttonDiscover=(Button)findViewById(R.id.buttonDiscover);
        buttonDiscover.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                discoverPeers();
            }
        });
        Button buttonEnable=(Button)findViewById(R.id.buttonEnable);
        buttonEnable.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                // ����wifi direct���Ա㱻�����豸���ֻ��������豸
                Intent intent=new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener(){
            
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                connectTo(deviceList.get(index));
            }
        });
    }
    
    private void initializeWiFiDirect() {
        // wifi direct��������ʹ��WifiP2pManagerϵͳ���������Ӻ͹����
        wifiP2pManager=(WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        // ����һ�����ӵ�wifi direct��ͨ��������ʹ�����ͨ����wifi direct���н���
        wifiDirectChannel=wifiP2pManager.initialize(this, getMainLooper(), new ChannelListener(){
            
            public void onChannelDisconnected() {
                // ͨ����ʧ
            }
        });
    }
    
    // -----------
    private ActionListener actionListener=new ActionListener(){
        
        // WiFiP2pManagetִ�д��������������ص�ActionListnner��ָʾ�Ƿ�ɹ�
        public void onFailure(int reason) {
            String errorMessage="WiFi Direct Failed: ";
            switch(reason){
                case WifiP2pManager.BUSY:
                    errorMessage+="Framework busy.";
                    break;
                case WifiP2pManager.ERROR:
                    errorMessage+="Internal error.";
                    break;
                case WifiP2pManager.P2P_UNSUPPORTED:
                    errorMessage+="Unsupported.";
                    break;
                default:
                    errorMessage+="Unknown error.";
                    break;
            }
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
        }
        
        public void onSuccess() {
        }
    };
    // ---------------
    BroadcastReceiver p2pStatusReceiver=new BroadcastReceiver(){
        
        // ����wifi direct������״̬
        @Override
        public void onReceive(Context context, Intent intent) {
            int state=intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
                                         WifiP2pManager.WIFI_P2P_STATE_DISABLED);
            switch(state){
                case (WifiP2pManager.WIFI_P2P_STATE_ENABLED):
                    buttonDiscover.setEnabled(true);
                    break;
                default:
                    buttonDiscover.setEnabled(false);
            }
        }
    };
    
    private void discoverPeers() {
        // ɨ������wifi direct�豸
        wifiP2pManager.discoverPeers(wifiDirectChannel, actionListener);
    }
    
    private List<WifiP2pDevice> deviceList=new ArrayList<WifiP2pDevice>();
    BroadcastReceiver peerDiscoveryReceiver=new BroadcastReceiver(){
        
        // ��ȡɨ����
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiP2pManager.requestPeers(wifiDirectChannel,
                                        new PeerListListener(){
                                            
                                            public void onPeersAvailable(WifiP2pDeviceList peers) {
                                                deviceList.clear();
                                                deviceList.addAll(peers.getDeviceList());
                                                aa.notifyDataSetChanged();
                                            }
                                        });
        }
    };
    
    // --------------
    private void connectTo(WifiP2pDevice device) {
        // �����豸
        WifiP2pConfig config=new WifiP2pConfig();
        config.deviceAddress=device.deviceAddress;
        wifiP2pManager.connect(wifiDirectChannel, config, actionListener);
    }
    
    BroadcastReceiver connectionChangedReceiver=new BroadcastReceiver(){
        
        // �������ӽ��
        @Override
        public void onReceive(Context context, Intent intent) {
            String extraKey=WifiP2pManager.EXTRA_NETWORK_INFO;
            NetworkInfo networkInfo=(NetworkInfo)intent.getParcelableExtra(extraKey);
            if(networkInfo.isConnected()){
                wifiP2pManager.requestConnectionInfo(wifiDirectChannel, new ConnectionInfoListener(){
                    //��ȡ���ӵ���ϸ��Ϣ
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if(info.groupFormed){
                            if(info.isGroupOwner){
                                // ����Ƿ�����
                                initiateServerSocket();
                            }
                            else if(info.groupFormed){
                                // ����ǿͻ���
                                initiateClientSocket(info.groupOwnerAddress.toString());
                            }
                        }
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Wi-Fi Direct Disconnected", Toast.LENGTH_SHORT);
            }
        }
    };
    
    private void initiateServerSocket() {
        // �����
        ServerSocket serverSocket;
        try{
            serverSocket=new ServerSocket(8666);
            Socket serverClient=serverSocket.accept();
        } catch(IOException e){
            Log.e(TAG, "I/O Exception", e);
        }
    }
    
    private void initiateClientSocket(String hostAddress) {
        // �ͻ���
        int timeout=10000;
        int port=8666;
        InetSocketAddress socketAddress=new InetSocketAddress(hostAddress, port);
        try{
            Socket socket=new Socket();
            socket.bind(null);
            socket.connect(socketAddress, timeout);
        } catch(IOException e){
            Log.e(TAG, "IO Exception.", e);
        }
    }
    
    @Override
    protected void onPause() {
        unregisterReceiver(peerDiscoveryReceiver);
        unregisterReceiver(connectionChangedReceiver);
        unregisterReceiver(p2pStatusReceiver);
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(peerDiscoveryReceiver, peerfilter);
        registerReceiver(connectionChangedReceiver, connectionfilter);
        registerReceiver(p2pStatusReceiver, p2pEnabled);
    }
}