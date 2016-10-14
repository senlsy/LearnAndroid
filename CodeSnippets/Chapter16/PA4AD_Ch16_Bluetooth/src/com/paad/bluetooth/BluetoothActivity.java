package com.paad.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;


public class BluetoothActivity extends Activity
{
    
    protected static final String TAG="BLUETOOTH";
    protected static final int DISCOVERY_REQUEST=1;
    BluetoothAdapter bluetooth;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        BluetoothAdapter bluetooth=BluetoothAdapter.getDefaultAdapter();
        this.bluetooth=bluetooth;
    }
    
    private static final int ENABLE_BLUETOOTH=1;
    
    private void initBluetooth() {
        // 请求打开蓝牙
        if( !bluetooth.isEnabled()){
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE_BLUETOOTH);
        }
        else{
            initBluetoothUI();
        }
    }
    
    private void makeDiscoverable() {
        // 请求从用户那里获得蓝牙的可发现性
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), DISCOVERY_REQUEST);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 请求打开蓝牙的回调
        if(requestCode == ENABLE_BLUETOOTH)
            if(resultCode == RESULT_OK){
                initBluetoothUI();
            }
        if(requestCode == DISCOVERY_REQUEST){
            if(resultCode == RESULT_CANCELED){
                Log.d(TAG, "Discovery cancelled by user");
            }
        }
    }
    
    private ArrayList<BluetoothDevice> deviceList=new ArrayList<BluetoothDevice>();
    
    private void startDiscovery() {
        // 启动蓝牙索搜，监听蓝牙发现通知
        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        if(bluetooth.isEnabled() && !bluetooth.isDiscovering())
            deviceList.clear();
        bluetooth.startDiscovery();
    }
    
    BroadcastReceiver discoveryResult=new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String remoteDeviceName=intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceList.add(remoteDevice);
            Log.d(TAG, "Discovered " + remoteDeviceName);
        }
    };
    private BluetoothSocket transferSocket;
    
    private UUID startServerSocket(BluetoothAdapter bluetooth) {
        // 启动一个蓝牙接收服务端
        UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        String name="bluetoothserver";
        try{
            final BluetoothServerSocket btserver=bluetooth.listenUsingRfcommWithServiceRecord(name, uuid);
            Thread acceptThread=new Thread(new Runnable(){
                
                public void run() {
                    try{
                        BluetoothSocket serverSocket=btserver.accept();
                        StringBuilder incoming=new StringBuilder();
                        listenForMessages(serverSocket, incoming);
                        transferSocket=serverSocket;
                    } catch(IOException e){
                        Log.e("BLUETOOTH", "Server connection IO Exception", e);
                    }
                }
            });
            acceptThread.start();
        } catch(IOException e){
            Log.e("BLUETOOTH", "Socket listener IO Exception", e);
        }
        return uuid;
    }
    
    private void connectToServerSocket(BluetoothDevice device, UUID uuid) {
        // 去连接一个蓝牙服务端
        try{
            BluetoothSocket clientSocket=device.createRfcommSocketToServiceRecord(uuid);
            clientSocket.connect();
            StringBuilder incoming=new StringBuilder();
            listenForMessages(clientSocket, incoming);
            transferSocket=clientSocket;
        } catch(IOException e){
            Log.e("BLUETOOTH", "Blueooth client I/O Exception", e);
        }
    }
    
    private void sendMessage(BluetoothSocket socket, String message) {
        OutputStream outStream;
        try{
            outStream=socket.getOutputStream();
            byte[] byteArray=(message + " ").getBytes();
            byteArray[byteArray.length - 1]=0;
            outStream.write(byteArray);
        } catch(IOException e){
            Log.e(TAG, "Message send failed.", e);
        }
    }
    
    private boolean listening=false;
    
    private void listenForMessages(BluetoothSocket socket,
                                   StringBuilder incoming) {
        listening=true;
        int bufferSize=1024;
        byte[] buffer=new byte[bufferSize];
        try{
            InputStream instream=socket.getInputStream();
            int bytesRead= -1;
            while(listening){
                bytesRead=instream.read(buffer);
                if(bytesRead != -1){
                    String result="";
                    while((bytesRead == bufferSize) &&
                          (buffer[bufferSize - 1] != 0)){
                        result=result + new String(buffer, 0, bytesRead - 1);
                        bytesRead=instream.read(buffer);
                    }
                    result=result + new String(buffer, 0, bytesRead - 1);
                    incoming.append(result);
                }
                socket.close();
            }
        } catch(IOException e){
            Log.e(TAG, "Message received failed.", e);
        } finally{
        }
    }
    
    private void initBluetoothUI() {
    }
}