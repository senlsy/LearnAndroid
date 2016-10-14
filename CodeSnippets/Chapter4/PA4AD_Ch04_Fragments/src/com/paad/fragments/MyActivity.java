package com.paad.fragments;

import com.paad.fragments.SeasonFragment.OnSeasonSelectedListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class MyActivity extends Activity implements OnSeasonSelectedListener
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
    public void onSeasonSelected() {
        Log.e("lintest", "MyActivity被回调了");
    }
}