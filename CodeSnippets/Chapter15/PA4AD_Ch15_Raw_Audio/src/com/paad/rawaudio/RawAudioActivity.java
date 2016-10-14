package com.paad.rawaudio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class RawAudioActivity extends Activity
{
    
    private static final String TAG="RAWAUDIO";
    private boolean isRecording=false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button record=(Button)findViewById(R.id.buttonRecord);
        Button play=(Button)findViewById(R.id.buttonPlayback);
        Button stop=(Button)findViewById(R.id.buttonStop);
        record.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                AsyncTask<Void, Void, Void> task=new AsyncTask<Void, Void, Void>(){
                    
                    @Override
                    protected Void doInBackground(Void... params) {
                        isRecording=true;
                        // 录制音频应该放到异步线程上做
                        record();
                        return null;
                    }
                };
                task.execute();
            }
        });
        play.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                AsyncTask<Void, Void, Void> task=new AsyncTask<Void, Void, Void>(){
                    
                    @Override
                    protected Void doInBackground(Void... params) {
                        playback();
                        return null;
                    }
                };
                task.execute();
            }
        });
        stop.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                isRecording=false;
            }
        });
    }
    
    private void record() {
        File file=new File(Environment.getExternalStorageDirectory(), "raw.pcm");
        try{
            file.createNewFile();
        } catch(IOException e){
            Log.d(TAG, "IO Exception", e);
        }
        try{
            OutputStream os=new FileOutputStream(file);
            BufferedOutputStream bos=new BufferedOutputStream(os);
            DataOutputStream dos=new DataOutputStream(bos);
            // 录制参数，这些录制参数并不会被附加到录制的音频文件中，所以播放的时候最好采用一直的参数
            int frequency=11025;
            int channelConfiguration=AudioFormat.CHANNEL_CONFIGURATION_MONO;
            int audioEncoding=AudioFormat.ENCODING_PCM_16BIT;
            // 创建录制音频所需最小的录制缓冲区
            int bufferSize=AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            short[] buffer=new short[bufferSize];
            // 初始化AudioRecord对象
            AudioRecord audioRecord=new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            // 开始录音
            audioRecord.startRecording();
            while(isRecording){
                // 将硬件录制的数据读取到缓冲区
                int bufferReadResult=audioRecord.read(buffer, 0, bufferSize);
                // 将缓冲区的数据写出到文件
                for(int i=0;i < bufferReadResult;i++)
                    dos.writeShort(buffer[i]);
            }
            // 录音停止
            audioRecord.stop();
            // 关闭流
            dos.close();
        } catch(Throwable t){
            Log.d(TAG, "An error occurred during recording", t);
        }
    }
    
    private void playback() {
        //
        File file=new File(Environment.getExternalStorageDirectory(), "raw.pcm");
        int audioLength=(int)(file.length() / 2);//short是2字节
        short[] audio=new short[audioLength];
        
        try{
            InputStream is=new FileInputStream(file);
            BufferedInputStream bis=new BufferedInputStream(is);
            DataInputStream dis=new DataInputStream(bis);
            int i=0;
            while(dis.available() > 0){
                audio[i]=dis.readShort();
                i++;
            }
            dis.close();
            // 播放参数，最好采用与录制音频时一致的录制参数
            int frequency=11025;
            int channelConfiguration=AudioFormat.CHANNEL_CONFIGURATION_MONO;
            int audioEncoding=AudioFormat.ENCODING_PCM_16BIT;
            AudioTrack audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC,
                                                 frequency, channelConfiguration, audioEncoding,
                                                 audioLength, AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.write(audio, 0, audioLength);
        } catch(Throwable t){
            Log.d(TAG, "An error occurred during playback", t);
        }
    }
}