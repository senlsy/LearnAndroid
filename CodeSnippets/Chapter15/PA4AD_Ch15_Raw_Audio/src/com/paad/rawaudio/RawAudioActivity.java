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
                        // ¼����ƵӦ�÷ŵ��첽�߳�����
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
            // ¼�Ʋ�������Щ¼�Ʋ��������ᱻ���ӵ�¼�Ƶ���Ƶ�ļ��У����Բ��ŵ�ʱ����ò���һֱ�Ĳ���
            int frequency=11025;
            int channelConfiguration=AudioFormat.CHANNEL_CONFIGURATION_MONO;
            int audioEncoding=AudioFormat.ENCODING_PCM_16BIT;
            // ����¼����Ƶ������С��¼�ƻ�����
            int bufferSize=AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            short[] buffer=new short[bufferSize];
            // ��ʼ��AudioRecord����
            AudioRecord audioRecord=new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            // ��ʼ¼��
            audioRecord.startRecording();
            while(isRecording){
                // ��Ӳ��¼�Ƶ����ݶ�ȡ��������
                int bufferReadResult=audioRecord.read(buffer, 0, bufferSize);
                // ��������������д�����ļ�
                for(int i=0;i < bufferReadResult;i++)
                    dos.writeShort(buffer[i]);
            }
            // ¼��ֹͣ
            audioRecord.stop();
            // �ر���
            dos.close();
        } catch(Throwable t){
            Log.d(TAG, "An error occurred during recording", t);
        }
    }
    
    private void playback() {
        //
        File file=new File(Environment.getExternalStorageDirectory(), "raw.pcm");
        int audioLength=(int)(file.length() / 2);//short��2�ֽ�
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
            // ���Ų�������ò�����¼����Ƶʱһ�µ�¼�Ʋ���
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