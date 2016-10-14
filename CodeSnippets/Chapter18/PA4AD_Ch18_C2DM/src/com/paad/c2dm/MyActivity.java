package com.paad.c2dm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


public class MyActivity extends Activity
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    private void registerWithC2DMServer() {
        Intent registrationIntent=new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        registrationIntent.putExtra("sender", "myC2DMaccount@gmail.com");
        startService(registrationIntent);
    }
}