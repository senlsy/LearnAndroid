package com.paad.mediaplayer;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AudioPlayerActivity extends Activity
{
    
    static final String TAG="PlayerActivity";
    private MediaPlayer mediaPlayer;
    private RemoteControlClient myRemoteControlClient;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioplayer);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 多媒体按钮音量调节哪个音频流
        // 初始化mediaPlayer
        try{
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setDataSource("/sdcard/test.mp3");
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener(){
                
                public void onCompletion(MediaPlayer mp) {
                    AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    am.abandonAudioFocus(focusChangeListener);
                }
            });
        } catch(IllegalArgumentException e){
            Log.d(TAG, "Illegal Argument Exception: " + e.getMessage());
        } catch(SecurityException e){
            Log.d(TAG, "Security Exception: " + e.getMessage());
        } catch(IllegalStateException e){
            Log.d(TAG, "Illegal State Exception: " + e.getMessage());
        } catch(IOException e){
            Log.d(TAG, "IO Exception: " + e.getMessage());
        }
        registerRemoteControlClient();// 注册远程控件按键的监听
        Button buttonPlay=(Button)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                play();
            }
        });
        Button buttonBass=(Button)findViewById(R.id.buttonBassBoost);
        buttonBass.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                bassBoost();
            }
        });
    }
    
    private void bassBoost() {
        // 增强低音？
        int sessionId=mediaPlayer.getAudioSessionId();
        short boostStrength=500;
        int priority=0;
        BassBoost bassBoost=new BassBoost(priority, sessionId);
        bassBoost.setStrength(boostStrength);
        bassBoost.setEnabled(true);
    }
    
    // ---------------------------
    private void registerRemoteControlClient() {
        // 注册Remote Control Client（远程控制控件，例如屏锁上的多媒体播放条）的多媒体按钮监听，
        // 当点击该类按键会触发设置的PendingIntent，
        // 并设置接收的Receiver为MediaControlReceiver
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        ComponentName component=new ComponentName(this, MediaControlReceiver.class);
        Intent intent=new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(component);
        PendingIntent mediaPendingIntent=PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        myRemoteControlClient=new RemoteControlClient(mediaPendingIntent);
        am.registerRemoteControlClient(myRemoteControlClient);
        // 设置应用程序支持那些多媒体按键，这里设置了支持开始暂停按键跟停止按键
        myRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP);
    }
    
    private void setRemoteControlMetadata(Bitmap artwork, String album, String artist, long trackNumber) {
        // 为RemoteControlClient远程控件设置一些可显示的信息
        MetadataEditor editor=myRemoteControlClient.editMetadata(false);
        editor.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, artwork);
        editor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, album);
        editor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, artist);
        editor.putLong(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER, trackNumber);
        editor.apply();
    }
    
    // ----------------------------
    public class ActivityMediaControlReceiver extends BroadcastReceiver
    {
        
        // 监听媒体按键
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MediaControlReceiver.ACTION_MEDIA_BUTTON.equals(intent.getAction())){
                KeyEvent event=(KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                switch(event.getKeyCode()){
                    case (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE):
                        if(mediaPlayer.isPlaying())
                            pause();
                        else
                            play();
                        break;
                    case (KeyEvent.KEYCODE_MEDIA_PLAY):
                        play();
                        break;
                    case (KeyEvent.KEYCODE_MEDIA_PAUSE):
                        pause();
                        break;
                    case (KeyEvent.KEYCODE_MEDIA_NEXT):
                        skip();
                        break;
                    case (KeyEvent.KEYCODE_MEDIA_PREVIOUS):
                        previous();
                        break;
                    case (KeyEvent.KEYCODE_MEDIA_STOP):
                        stop();
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    private ActivityMediaControlReceiver activityMediaControlReceiver;
    
    @Override
    protected void onResume() {
        super.onResume();
        // 注册当前应用程序为多媒体按键的唯一接收程序（MediaControlReceiver）
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        ComponentName component=new ComponentName(this, MediaControlReceiver.class);
        am.registerMediaButtonEventReceiver(component);
        // 注册接收MediaControlReceiver发出的广播
        activityMediaControlReceiver=new ActivityMediaControlReceiver();
        IntentFilter filter=new IntentFilter(MediaControlReceiver.ACTION_MEDIA_BUTTON);
        registerReceiver(activityMediaControlReceiver, filter);
        // 拔出耳机带，输出流切换到扬声器的监听---------------------------------
        IntentFilter noiseFilter=new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(new NoisyAudioStreamReceiver(), noiseFilter);
    }
    
    private class NoisyAudioStreamReceiver extends BroadcastReceiver
    {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())){
                pause();
            }
        }
    }
    
    private OnAudioFocusChangeListener focusChangeListener=new OnAudioFocusChangeListener(){
        
        public void onAudioFocusChange(int focusChange) {
            AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            switch(focusChange){
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                    // 暂时失去音频焦点，并且是否可以进入ducking状态（调低音量来处理）
                    mediaPlayer.setVolume(0.2f, 0.2f);
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                    // 暂时失去音频焦点
                    pause();
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS):
                    //永久性丢失的时候，最好停止播放，并释放音频焦点的监听。只有让用户选择才重新播放
                    stop();
                    // 完全失去当前音频焦点,最好也取消应用程序是当前多媒体按键的唯一接收者。
                    ComponentName component=new ComponentName(AudioPlayerActivity.this, MediaControlReceiver.class);
                    am.unregisterMediaButtonEventReceiver(component);
                    break;
                case (AudioManager.AUDIOFOCUS_GAIN):
                    // 对于暂时丢失之后，再次获得音频焦点
                    mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.start();
                    break;
                default:
                    break;
            }
        }
    };
    
    public void play() {
        // 请求获取移动设备当前音频焦点
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result=am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
        }
        // 设置RemoteControlClient（远程控件，例如屏锁上的多媒体播放条）为播放状态
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
    }
    
    public void stop() {
        mediaPlayer.stop();
        // 设置RemoteControlClient（远程控件，例如屏锁上的多媒体播放条）为停止状态
        myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
        // 放弃当前音频焦点。
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(focusChangeListener);
    }
    
    public void pause() {
        mediaPlayer.pause();
        // 设置远程控件出去暂停状态
        myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
    }
    
    public void skip() {
        // 快进
    }
    
    public void previous() {
        // 快退
    }
}