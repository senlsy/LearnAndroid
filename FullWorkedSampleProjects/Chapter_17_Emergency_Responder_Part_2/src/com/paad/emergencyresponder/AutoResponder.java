package com.paad.emergencyresponder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.res.Resources;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


public class AutoResponder extends Activity
{
    
    public static final String autoResponsePref="autoResponsePref";// 是否自动回复
    public static final String responseTextPref="responseTextPref";// 自动回复文本
    public static final String includeLocPref="includeLocPref";// 是否包含位置信息
    public static final String respondForPref="respondForPref";//设置几分钟内自动回复
    public static final String defaultResponseText="All clear";
    
    Spinner respondForSpinner;
    CheckBox locationCheckbox;
    EditText responseTextBox;
    PendingIntent intentToFire;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoresponder);
        respondForSpinner=(Spinner)findViewById(R.id.spinnerRespondFor);
        locationCheckbox=(CheckBox)findViewById(R.id.checkboxLocation);
        responseTextBox=(EditText)findViewById(R.id.responseText);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.respondForDisplayItems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        respondForSpinner.setAdapter(adapter);
        Button okButton=(Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener(){
            
            public void onClick(View view) {
                savePreferences();
                setResult(RESULT_OK, null);
                finish();
            }
        });
        Button cancelButton=(Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            
            public void onClick(View view) {
                respondForSpinner.setSelection( -1);
                savePreferences();
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
        updateUIFromPreferences();
    }
    
    private void updateUIFromPreferences() {
        String preferenceName=getString(R.string.user_preferences);
        SharedPreferences sp=getSharedPreferences(preferenceName, 0);
        boolean autoRespond=sp.getBoolean(autoResponsePref, false);
        String respondText=sp.getString(responseTextPref, defaultResponseText);
        boolean includeLoc=sp.getBoolean(includeLocPref, false);
        int respondForIndex=sp.getInt(respondForPref, 0);
        if(autoRespond)
            respondForSpinner.setSelection(respondForIndex);
        else
            respondForSpinner.setSelection(0);
        locationCheckbox.setChecked(includeLoc);
        responseTextBox.setText(respondText);
    }
    
    private void savePreferences() {
        boolean autoRespond=respondForSpinner.getSelectedItemPosition() > 0;
        int respondForIndex=respondForSpinner.getSelectedItemPosition();
        boolean includeLoc=locationCheckbox.isChecked();
        String respondText=responseTextBox.getText().toString();//自动回复文本
        String preferenceName=getString(R.string.user_preferences);
        SharedPreferences sp=getSharedPreferences(preferenceName, 0);
        Editor editor=sp.edit();
        editor.putBoolean(autoResponsePref, autoRespond);
        editor.putString(responseTextPref, respondText);
        editor.putBoolean(includeLocPref, includeLoc);
        editor.putInt(respondForPref, respondForIndex);
        editor.commit();
        setAlarm(respondForIndex);
    }
    
    public static final String alarmAction="com.paad.emergencyresponder.AUTO_RESPONSE_EXPIRED";
    private void setAlarm(int respondForIndex) {
        // Create the alarm and register the alarm intent receiver.
        AlarmManager alarms=(AlarmManager)getSystemService(ALARM_SERVICE);
        if(intentToFire == null){
            Intent intent=new Intent(alarmAction);
            intentToFire=PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            IntentFilter filter=new IntentFilter(alarmAction);
            registerReceiver(stopAutoResponderReceiver, filter);
        }
        if(respondForIndex < 1){
            alarms.cancel(intentToFire);// 小于1就取消定时回复一次
        }
        else{
            Resources r=getResources();
            int[] respondForValues=r.getIntArray(R.array.respondForValues);
            int respondFor=respondForValues[respondForIndex];
            long t=System.currentTimeMillis();
            t=t + respondFor * 1000 * 60;
            alarms.set(AlarmManager.RTC_WAKEUP, t, intentToFire);
        }
    }
    
    private BroadcastReceiver stopAutoResponderReceiver=new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(alarmAction)){
                String preferenceName=getString(R.string.user_preferences);
                SharedPreferences sp=getSharedPreferences(preferenceName, 0);
                Editor editor=sp.edit();
                editor.putBoolean(autoResponsePref, false);
                editor.commit();
            }
        }
    };
}