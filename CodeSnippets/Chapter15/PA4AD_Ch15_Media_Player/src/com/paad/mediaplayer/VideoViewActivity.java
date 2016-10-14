package com.paad.mediaplayer;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoViewActivity extends Activity
{
    
    static final String TAG="PlayerActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoviewer);
        configureVideoView();
    }
    
    private void prepareMediaPlayer() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        MediaPlayer mediaPlayer=new MediaPlayer();
        mediaPlayer.setDataSource("/sdcard/mydopetunes.mp3");
        mediaPlayer.prepare();
    }
    
    private void configureVideoView() {
        final VideoView videoView=(VideoView)findViewById(R.id.videoView);
        videoView.setKeepScreenOn(true);
        videoView.setVideoPath("/sdcard/mycatvideo.3gp");
        MediaController mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
    }
}