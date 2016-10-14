package com.paad.compass;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class MyActivity extends Activity implements OnClickListener
{
    
    /** Called when the activity is first created. */
    CompassView view;
    
    public float value=10f;
    
    float bearing, pitch, roll;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        view=(CompassView)this.findViewById(R.id.compassView);
        this.findViewById(R.id.bearing1).setOnClickListener(this);
        this.findViewById(R.id.bearing2).setOnClickListener(this);
        this.findViewById(R.id.pitch1).setOnClickListener(this);
        this.findViewById(R.id.pitch2).setOnClickListener(this);
        this.findViewById(R.id.roll1).setOnClickListener(this);
        this.findViewById(R.id.roll2).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bearing1:
                bearing+=value;
                view.setBearing(bearing);
                break;
            case R.id.bearing2:
                bearing-=value;
                view.setBearing(bearing);
                break;
            case R.id.pitch1:
                pitch+=value;
                view.setPitch(pitch);
                break;
            case R.id.pitch2:
                pitch-=value;
                view.setPitch(pitch);
                break;
            case R.id.roll1:
                roll+=value;
                view.setRoll(roll);
                break;
            case R.id.roll2:
                roll-=value;
                view.setRoll(roll);
                break;
            default:
                break;
        }
        view.invalidate();
    }
}