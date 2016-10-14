package com.paad.intentvideocamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.VideoView;

import com.paad.intentcamera.R;


public class VideoCameraActivity extends Activity
{
    
    private static final int RECORD_VIDEO=0;
    
    private void startRecording() {
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, RECORD_VIDEO);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RECORD_VIDEO){
            VideoView videoView=(VideoView)findViewById(R.id.videoView);
            videoView.setVideoURI(data.getData());
            videoView.start();
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button videoButton=(Button)findViewById(R.id.buttonVideo);
        videoButton.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                startRecording();
            }
        });
    }
}