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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// ��ý�尴ť���������ĸ���Ƶ��
        // ��ʼ��mediaPlayer
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
        registerRemoteControlClient();// ע��Զ�̿ؼ������ļ���
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
        // ��ǿ������
        int sessionId=mediaPlayer.getAudioSessionId();
        short boostStrength=500;
        int priority=0;
        BassBoost bassBoost=new BassBoost(priority, sessionId);
        bassBoost.setStrength(boostStrength);
        bassBoost.setEnabled(true);
    }
    
    // ---------------------------
    private void registerRemoteControlClient() {
        // ע��Remote Control Client��Զ�̿��ƿؼ������������ϵĶ�ý�岥�������Ķ�ý�尴ť������
        // ��������ఴ���ᴥ�����õ�PendingIntent��
        // �����ý��յ�ReceiverΪMediaControlReceiver
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        ComponentName component=new ComponentName(this, MediaControlReceiver.class);
        Intent intent=new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(component);
        PendingIntent mediaPendingIntent=PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        myRemoteControlClient=new RemoteControlClient(mediaPendingIntent);
        am.registerRemoteControlClient(myRemoteControlClient);
        // ����Ӧ�ó���֧����Щ��ý�尴��������������֧�ֿ�ʼ��ͣ������ֹͣ����
        myRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP);
    }
    
    private void setRemoteControlMetadata(Bitmap artwork, String album, String artist, long trackNumber) {
        // ΪRemoteControlClientԶ�̿ؼ�����һЩ����ʾ����Ϣ
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
        
        // ����ý�尴��
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
        // ע�ᵱǰӦ�ó���Ϊ��ý�尴����Ψһ���ճ���MediaControlReceiver��
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        ComponentName component=new ComponentName(this, MediaControlReceiver.class);
        am.registerMediaButtonEventReceiver(component);
        // ע�����MediaControlReceiver�����Ĺ㲥
        activityMediaControlReceiver=new ActivityMediaControlReceiver();
        IntentFilter filter=new IntentFilter(MediaControlReceiver.ACTION_MEDIA_BUTTON);
        registerReceiver(activityMediaControlReceiver, filter);
        // �γ���������������л����������ļ���---------------------------------
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
                    // ��ʱʧȥ��Ƶ���㣬�����Ƿ���Խ���ducking״̬����������������
                    mediaPlayer.setVolume(0.2f, 0.2f);
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                    // ��ʱʧȥ��Ƶ����
                    pause();
                    break;
                case (AudioManager.AUDIOFOCUS_LOSS):
                    //�����Զ�ʧ��ʱ�����ֹͣ���ţ����ͷ���Ƶ����ļ�����ֻ�����û�ѡ������²���
                    stop();
                    // ��ȫʧȥ��ǰ��Ƶ����,���Ҳȡ��Ӧ�ó����ǵ�ǰ��ý�尴����Ψһ�����ߡ�
                    ComponentName component=new ComponentName(AudioPlayerActivity.this, MediaControlReceiver.class);
                    am.unregisterMediaButtonEventReceiver(component);
                    break;
                case (AudioManager.AUDIOFOCUS_GAIN):
                    // ������ʱ��ʧ֮���ٴλ����Ƶ����
                    mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.start();
                    break;
                default:
                    break;
            }
        }
    };
    
    public void play() {
        // �����ȡ�ƶ��豸��ǰ��Ƶ����
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result=am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.start();
        }
        // ����RemoteControlClient��Զ�̿ؼ������������ϵĶ�ý�岥������Ϊ����״̬
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
    }
    
    public void stop() {
        mediaPlayer.stop();
        // ����RemoteControlClient��Զ�̿ؼ������������ϵĶ�ý�岥������Ϊֹͣ״̬
        myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
        // ������ǰ��Ƶ���㡣
        AudioManager am=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(focusChangeListener);
    }
    
    public void pause() {
        mediaPlayer.pause();
        // ����Զ�̿ؼ���ȥ��ͣ״̬
        myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
    }
    
    public void skip() {
        // ���
    }
    
    public void previous() {
        // ����
    }
}