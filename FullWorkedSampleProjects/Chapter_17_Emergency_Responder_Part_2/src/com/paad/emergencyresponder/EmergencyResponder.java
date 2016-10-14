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
import android.content.SharedPreferences;
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
        Button autoResponderButton=
                                   (Button)findViewById(R.id.autoResponder);
        autoResponderButton.setOnClickListener(new View.OnClickListener(){
            
            public void onClick(View view) {
                startAutoResponder();
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter(SMS_RECEIVED);
        registerReceiver(emergencyResponseRequestReceiver, filter);
        //
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
        String okString="����ܰ�ȫ������Ҫ���";
        String notOkString="�����Σ�գ���Ҫ���";
        String outString=ok ? okString : notOkString;
        ArrayList<String> requestersCopy=(ArrayList<String>)requesters.clone();
        for(String to:requestersCopy)
            respond(to, outString, includeLocation);
    }
    
    private void respond(String to, String response, boolean includeLocation) {
        lock.lock();// ��Դͬ����������
        requesters.remove(to);
        aa.notifyDataSetChanged();
        lock.unlock();// ����
        SmsManager sms=SmsManager.getDefault();
        Intent intent=new Intent(SENT_SMS);
        intent.putExtra("recipient", to);
        PendingIntent sentPI=PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        sms.sendTextMessage(to, null, response, sentPI, null);
        StringBuilder sb=new StringBuilder();
        if(includeLocation){
            String ls=Context.LOCATION_SERVICE;
            LocationManager lm=(LocationManager)getSystemService(ls);
            Location l=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(l == null)
                sb.append("Location unknown.");
            else{
                sb.append("�����һ�����ڵ�λ��:");
                sb.append(l.toString() + "\n");
                ArrayList<String> locationMsgs=sms.divideMessage(sb.toString());
                for(String locationMsg:locationMsgs){
                    sms.sendTextMessage(to, null, locationMsg, null, null);
                }
            }
        }
    }
    
    private void startAutoResponder() {
        startActivityForResult(new Intent(EmergencyResponder.this, AutoResponder.class), 0);
    }
    
    public static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    BroadcastReceiver emergencyResponseRequestReceiver=new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(SMS_RECEIVED)){
                String queryString=getString(R.string.querystring).toLowerCase();
                Bundle bundle=intent.getExtras();
                if(bundle != null){
                    Object[] pdus=(Object[])bundle.get("pdus");
                    SmsMessage[] messages=new SmsMessage[pdus.length];
                    for(int i=0;i < pdus.length;i++)
                        messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
                    for(SmsMessage message:messages){
                        if(message.getMessageBody().toLowerCase().contains(queryString))
                            requestReceived(message.getOriginatingAddress());
                    }
                }
            }
        }
    };
    
    public void requestReceived(String from) {
        if( !requesters.contains(from)){
            lock.lock();
            requesters.add(from);
            aa.notifyDataSetChanged();
            lock.unlock();
            // �Ƿ��Զ��ظ�
            String preferenceName=getString(R.string.user_preferences);
            SharedPreferences prefs=getSharedPreferences(preferenceName, 0);
            boolean autoRespond=prefs.getBoolean(AutoResponder.autoResponsePref, false);
            if(autoRespond){
                String respondText=prefs.getString(AutoResponder.responseTextPref, AutoResponder.defaultResponseText);
                boolean includeLoc=prefs.getBoolean(AutoResponder.includeLocPref, false);
                respond(from, respondText, includeLoc);
            }
        }
    }
    
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
}