package com.paad.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;


public class BeamerActivity extends Activity
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
        //nfcAdapter会将拦截到的Intent填充到这个PendingIntent，以便传给你制定的组建
        Intent nfcIntent=new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent=PendingIntent.getActivity(this, 0, nfcIntent, 0);
        // 想要拦截的Intent数组
        IntentFilter tagIntentFilter=new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        tagIntentFilter.addDataScheme("http");
        tagIntentFilter.addDataAuthority("blog.radioactiveyak.com", null);
        intentFiltersArray=new IntentFilter[]{tagIntentFilter};
        // 想要处理标签的技术数组
        techListsArray=new String[][]{new String[]{NfcF.class.getName()}
        };
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 指定 该Activity当处于前台的时候，该应用程序将成为默认接收nfcIntent的默认应用程序
        //然后nfcAdapter将拦截到的Intent填充到PendingIntent，并触发该PendingIntent。
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, intentFiltersArray, techListsArray);
        //
        String action=getIntent().getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            processIntent(getIntent());
        }
    }
    
    private void processIntent(Intent intent) {
        String action=getIntent().getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            //提起nfc额外数据
            Parcelable[] messages=intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            for(int i=0;i < messages.length;i++){
                NdefMessage message=(NdefMessage)messages[i];
                NdefRecord[] records=message.getRecords();
                for(int j=0;j < records.length;j++){
                    NdefRecord record=records[j];
                }
            }
        }
    }
    
    public void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }
}