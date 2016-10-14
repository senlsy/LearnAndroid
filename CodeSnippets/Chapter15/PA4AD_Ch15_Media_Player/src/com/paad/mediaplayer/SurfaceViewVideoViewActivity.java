package com.paad.mediaplayer;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;


public class SurfaceViewVideoViewActivity extends Activity implements SurfaceHolder.Callback
{
    
    static final String TAG="SurfaceViewVideoViewActivity";
    private MediaPlayer mediaPlayer;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surfaceviewvideoviewer);
        mediaPlayer=new MediaPlayer();
        //
        final SurfaceView surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        surfaceView.setKeepScreenOn(true);
        SurfaceHolder holder=surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(400, 300);
        // Connect a play button.
        Button playButton=(Button)findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });
        // Connect a pause button.
        Button pauseButton=(Button)findViewById(R.id.buttonPause);
        pauseButton.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });
        // Add a skip button.
        Button skipButton=(Button)findViewById(R.id.buttonSkip);
        skipButton.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getDuration() / 2);
            }
        });
        MediaController mediaController=new MediaController(this);
        mediaController.setMediaPlayer(new MediaPlayerControl(){
            
            public boolean canPause() {
                return true;
            }
            
            public boolean canSeekBackward() {
                return true;
            }
            
            public boolean canSeekForward() {
                return true;
            }
            
            public int getBufferPercentage() {
                return 0;
            }
            
            public int getCurrentPosition() {
                return mediaPlayer.getCurrentPosition();
            }
            
            public int getDuration() {
                return mediaPlayer.getDuration();
            }
            
            public boolean isPlaying() {
                return mediaPlayer.isPlaying();
            }
            
            public void pause() {
                mediaPlayer.pause();
            }
            
            public void seekTo(int pos) {
                mediaPlayer.seekTo(pos);
            }
            
            public void start() {
                mediaPlayer.start();
            }
        });
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            mediaPlayer.setDisplay(holder);
            mediaPlayer.setDataSource("/sdcard/test2.3gp");
            mediaPlayer.prepare();
        } catch(IllegalArgumentException e){
            Log.e(TAG, "Illegal Argument Exception", e);
        } catch(IllegalStateException e){
            Log.e(TAG, "Illegal State Exception", e);
        } catch(SecurityException e){
            Log.e(TAG, "Security Exception", e);
        } catch(IOException e){
            Log.e(TAG, "IO Exception", e);
        }
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaPlayer.release();
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
}