package com.paad.earthquake.preferences;

import java.util.List;

import com.paad.earthquake.R;
import com.paad.earthquake.R.xml;

import android.preference.PreferenceActivity;

public class FragmentPreferences extends PreferenceActivity {

  @Override
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);
  }
}