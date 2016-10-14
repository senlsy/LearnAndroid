package com.paad.preferences.fragment;

import com.paad.preferences.R;
import com.paad.preferences.R.xml;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class MyPreferenceFragment extends PreferenceFragment
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PreferenceFragmentÌî³äPrefernceScreen
        addPreferencesFromResource(R.xml.userpreferences);
    }
}