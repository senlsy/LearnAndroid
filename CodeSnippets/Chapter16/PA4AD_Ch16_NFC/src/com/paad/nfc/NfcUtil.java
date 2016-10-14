package com.paad.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.Toast;


public class NfcUtil
{
    
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private Context context;
    private static final byte[] HEX_CHAR_TABLE={(byte)'0',(byte)'1',
                                                (byte)'2',(byte)'3',(byte)'4',(byte)'5',(byte)'6',
                                                (byte)'7',(byte)'8',(byte)'9',(byte)'A',(byte)'B',
                                                (byte)'C',(byte)'D',(byte)'E',(byte)'F'};
    
    public NfcUtil(Context context){
        this.context=context;
        try{
            mAdapter=NfcAdapter.getDefaultAdapter(context);
            if(mAdapter == null){
                Toast.makeText(context, "设备不支持nfc", Toast.LENGTH_SHORT);
                return;
            }
            if( !mAdapter.isEnabled()){
                context.startActivity(new Intent("android.settings.NFC_SETTINGS"));
                return;
            }
            mPendingIntent=PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndefDetected=new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            ndefDetected.addDataType("text/plain");
            IntentFilter techDetected=new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            techDetected.addDataType("*/*");
            IntentFilter tagDetected=new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            mFilters=new IntentFilter[]{ndefDetected,techDetected,tagDetected};
        } catch(Exception e){
            Toast.makeText(context, "nfc初始化失败", Toast.LENGTH_SHORT);
        }
    }
    
    public void resume() {
        if(context == null){
            return;
        }
        try{
            Activity myActivity=(Activity)context;
            if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(myActivity.getIntent().getAction())
               || NfcAdapter.ACTION_TAG_DISCOVERED.equals(myActivity.getIntent().getAction())
               || NfcAdapter.ACTION_TECH_DISCOVERED.equals(myActivity.getIntent().getAction())){
                myActivity.setIntent(new Intent());
            }
            if(mAdapter != null){
                mAdapter.enableForegroundDispatch(myActivity, mPendingIntent, mFilters, null);
            }
        } catch(Exception e){
            Toast.makeText(context, "nfc模块异常", Toast.LENGTH_SHORT);
        }
    }
    
    public void pause() {
        if(mAdapter != null){
            mAdapter.disableForegroundDispatch((Activity)context);
        }
    }
    
    public String rfidCode(Intent intent) {
        try{
            //nfc原始对象数据。nfc标签自带的
            Tag tag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] rfid_byte=tag.getId();
            return getHexString(rfid_byte, rfid_byte.length);
        } catch(Exception e){
            return "";
        }
    }
    
    public String getHexString(byte[] raw, int len) {
        byte[] hex=new byte[2 * len];
        int index=0;
        int pos=0;
        for(byte b:raw){
            if(pos >= len)
                break;
            pos++;
            int v=b & 0xFF;
            hex[index++]=HEX_CHAR_TABLE[v >>> 4];
            hex[index++]=HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex);
    }
}
