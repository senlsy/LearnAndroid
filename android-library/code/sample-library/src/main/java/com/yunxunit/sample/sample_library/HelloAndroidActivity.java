package com.yunxunit.sample.sample_library;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class HelloAndroidActivity extends Activity implements OnClickListener
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void onClick(View v) {
    }
}
