package com.paad.emergencyresponder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;


public class EmergencyResponder extends Activity
{
    
    public static final String SENT_SMS="com.paad.emergencyresponder.SMS_SENT";
    // ReentrantLock是一个互斥的同步器，其实现了接口Lock，里面的功能函数主要有：
    // 1. ‍lock() -- 阻塞模式获取资源
    // 2. ‍lockInterruptibly() -- 可中断模式获取资源
    // 3. ‍tryLock() -- 尝试获取资源
    // 4. tryLock(time) -- 在一段时间内尝试获取资源
    // 5. ‍unlock() -- 释放资源
    ReentrantLock lock;
    CheckBox locationCheckBox;
    ArrayList<String> requesters;
    ArrayAdapter<String> aa;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lock=new ReentrantLock();
        requesters=new ArrayList<String>();
        locationCheckBox=(CheckBox)findViewById(R.id.checkboxSendLocation);
        ListView myListView=(ListView)findViewById(R.id.myListView);
        int layoutID=android.R.layout.simple_list_item_1;
        aa=new ArrayAdapter<String>(this, layoutID, requesters);
        myListView.setAdapter(aa);
        Button okButton=(Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener(){
            
            public void onClick(View view) {
                respond(true, locationCheckBox.isChecked());
            }
        });
        Button notOkButton=(Button)findViewById(R.id.notOkButton);
        notOkButton.setOnClickListener(new View.OnClickListener(){
            
            public void onClick(View view) {
                respond(false, locationCheckBox.isChecked());
            }
        });
        Button autoResponderButton=(Button)findViewById(R.id.autoResponder);
        autoResponderButton.setOnClickListener(new View.OnClickListener(){
            
            public void onClick(View view) {
                startAutoResponder();
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 信息接收监听-------
        IntentFilter filter=new IntentFilter(SMS_RECEIVED);
        registerReceiver(emergencyResponseRequestReceiver, filter);
        // 对发送时，发送的成功与否监听-------
        IntentFilter attemptedDeliveryfilter=new IntentFilter(SENT_SMS);
        registerReceiver(attemptedDeliveryReceiver, attemptedDeliveryfilter);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(emergencyResponseRequestReceiver);
        unregisterReceiver(attemptedDeliveryReceiver);
    }
    
    public void respond(boolean ok, boolean includeLocation) {
        String okString="地球很安全，不需要求救";
        String notOkString="地球很危险，需要求救";
        String outString=ok ? okString : notOkString;
        ArrayList<String> requestersCopy=(ArrayList<String>)requesters.clone();
        for(String to:requestersCopy){
            // 发给list列表中的每个人
            respond(to, outString, includeLocation);
        }
    }
    
    private void respond(String to, String response, boolean includeLocation) {
        lock.lock();// 上锁
        requesters.remove(to);
        aa.notifyDataSetChanged();
        lock.unlock();// 解锁
        SmsManager sms=SmsManager.getDefault();
        Intent intent=new Intent(SENT_SMS);
        intent.putExtra("recipient", to);
        // 发送是否成功触发的PendingIntent
        PendingIntent sentPI=PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        sms.sendTextMessage(to, null, response, sentPI, null);
        //
        StringBuilder sb=new StringBuilder();
        if(includeLocation){
            String ls=Context.LOCATION_SERVICE;
            LocationManager lm=(LocationManager)getSystemService(ls);
            Location l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(l == null)
                sb.append("Location unknown.");
            else{
                sb.append("我最近一次所在的位置:");
                sb.append(l.toString() + "\n");
                // 信息长度过长时，对信息进行切割
                ArrayList<String> locationMsgs=sms.divideMessage(sb.toString());
                for(String locationMsg:locationMsgs){
                    // 发送
                    sms.sendTextMessage(to, null, locationMsg, null, null);
                }
            }
        }
    }
    
    private void startAutoResponder() {
    }
    
    public static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    BroadcastReceiver emergencyResponseRequestReceiver=new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SMS_RECEIVED)){
                String queryString="are you OK?".toLowerCase();
                Bundle bundle=intent.getExtras();
                if(bundle != null){
                    // 提取接收到的信息
                    Object[] pdus=(Object[])bundle.get("pdus");
                    SmsMessage[] messages=new SmsMessage[pdus.length];
                    for(int i=0;i < pdus.length;i++)
                        messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
                    for(SmsMessage message:messages){
                        if(message.getMessageBody().toLowerCase().contains(queryString))
                            requestReceived(message.getOriginatingAddress());// 发信人号码
                    }
                }
            }
        }
    };
    private BroadcastReceiver attemptedDeliveryReceiver=new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context _context, Intent _intent) {
            if(_intent.getAction().equals(SENT_SMS)){
                if(getResultCode() != Activity.RESULT_OK){
                    String recipient=_intent.getStringExtra("recipient");
                    requestReceived(recipient);
                }
            }
        }
    };
    //刷新发送人联系列表
    public void requestReceived(String from) {
        if( !requesters.contains(from)){
            lock.lock();
            requesters.add(from);
            aa.notifyDataSetChanged();
            lock.unlock();
        }
    }
}