package com.paad.nfcbeam;

import java.util.Set;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;


public class ReceiverActivity extends Activity
{
    
    PendingIntent nfcPendingIntent;
    IntentFilter[] intentFiltersArray;
    String[][] techListsArray;
    NfcAdapter nfcAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        // 打包目标Intent的Intent，即nfcAdapter会将拦截到的Intent填充到这个PendingIntent，以传给你的应用程序
        Intent nfcIntent=new Intent();
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent=PendingIntent.getActivity(this, 0, nfcIntent, 0);
    }
    
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 使用前台标签分配系统
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
        //-----
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
            processIntent(getIntent());
        }
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            processIntent(getIntent());
        }
    }
    
    void processIntent(Intent intent) {
        Log.e("lintest", "action=" + intent.getAction());
        Log.e("lintest", "uri=" + intent.getDataString());
        Set<String> categorys=intent.getCategories();
        if(categorys != null)
        {
            for(String category:categorys)
            {
                Log.e("lintest", "category=" + intent.getDataString());
            }
        }
        Bundle bundle=intent.getExtras();
        if(bundle != null){
            Set<String> keys=bundle.keySet();
            for(String key:keys)
            {
                Log.e("lintest", "   " + key + ":" + bundle.get(key).toString());
            }
        }
        // ----------------------
        Parcelable[] messages=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage message=(NdefMessage)messages[0];
        NdefRecord record=message.getRecords()[0];
        String payload=new String(record.getPayload());
        TextView textView=(TextView)findViewById(R.id.textView);
        textView.setText(payload);
    }
}