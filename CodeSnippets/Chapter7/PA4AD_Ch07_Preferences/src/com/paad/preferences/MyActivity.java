package com.paad.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class MyActivity extends Activity
{
    
    private static final int SHOW_PREFERENCES_REQUESTCODE=0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Class c=Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? MyPreferenceActivity.class : MyFragmentPreferenceActivity.class;
        Log.e("lintest", "Build.VERSION.SDK_INT ="+Build.VERSION.SDK_INT +"   Build.VERSION_CODES.HONEYCOMB="+Build.VERSION_CODES.HONEYCOMB);
        Intent i=new Intent(this, c);
        startActivityForResult(i, SHOW_PREFERENCES_REQUESTCODE);
    }
}