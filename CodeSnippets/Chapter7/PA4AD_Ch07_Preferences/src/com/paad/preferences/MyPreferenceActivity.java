package com.paad.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class MyPreferenceActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferenceActivityֱ�����PrefernceSrceen
        addPreferencesFromResource(R.xml.userpreferences);
    }
}