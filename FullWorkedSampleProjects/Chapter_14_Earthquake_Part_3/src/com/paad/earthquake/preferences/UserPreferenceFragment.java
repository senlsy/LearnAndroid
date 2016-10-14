package com.paad.earthquake.preferences;

import com.paad.earthquake.R;
import com.paad.earthquake.R.xml;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public  class UserPreferenceFragment extends PreferenceFragment {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.userpreferences);
  }

}
