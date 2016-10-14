package com.paad.nfcbeam;

import java.nio.charset.Charset;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;


public class BeamerActivity extends Activity
{
    
    NfcAdapter nfcAdapter;
    NdefMessage nfcMessage;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        // 传输不容易被解释为uri的数据bean
        String payload="Two to beam across";
        String mimeType="application/com.paad.nfcbeam";
        byte[] mimeBytes=mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord ndefRecord1=new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload.getBytes());
        // 传输aar数据bean
        NdefRecord ndefRecord2=NdefRecord.createApplicationRecord("com.paad.nfcbeam");
        //
        NdefRecord[] ndferecorders=new NdefRecord[]{ndefRecord1,ndefRecord2};
        nfcMessage=new NdefMessage(ndferecorders);
        // 通过发送前回调， 动态改变发送的android bean
        nfcAdapter.setNdefPushMessageCallback(new CreateNdefMessageCallback(){
            
            public NdefMessage createNdefMessage(NfcEvent event) {
                String payload="Beam me up, Android!\n\n" + "Beam Time: " + System.currentTimeMillis();
                String mimeType="application/com.paad.nfcbeam";
                byte[] mimeBytes=mimeType.getBytes(Charset.forName("US-ASCII"));
                NdefMessage msg=new NdefMessage(new NdefRecord[]{new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload.getBytes()),
                                                                 NdefRecord.createApplicationRecord("com.paad.nfcbeam")
                });
                return msg;
            }
        }, this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 将创建的nfcMessage指定为android bean
        nfcAdapter.setNdefPushMessage(nfcMessage, this);
    }
}