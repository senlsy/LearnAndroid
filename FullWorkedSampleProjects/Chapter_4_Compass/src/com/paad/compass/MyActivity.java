package com.paad.compass;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MyActivity extends Activity
{
    
    public static float beraing;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final CompassView compassView=(CompassView)this.findViewById(R.id.compassView);
        Button button=(Button)this.findViewById(R.id.test);
        button.setOnClickListener(new OnClickListener(){
            
            @Override
            public void onClick(View v) {
                beraing=beraing + 30f;
                compassView.setBearing(beraing % 360);
                compassView.invalidate();
            }
        });
    }
}