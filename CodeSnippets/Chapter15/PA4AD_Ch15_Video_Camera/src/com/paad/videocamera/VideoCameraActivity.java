package com.paad.videocamera;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class VideoCameraActivity extends Activity implements
        SurfaceHolder.Callback
{
    
    private static final String TAG="CameraActivity";
    private Camera camera;
    private SurfaceHolder holder;
    private MediaRecorder mediaRecorder;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SurfaceView surface=(SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder holder=surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(400, 300);
        //
        Button record=(Button)findViewById(R.id.buttonRecord);
        Button stabilize=(Button)findViewById(R.id.buttonStabilize);
        Button scanner=(Button)findViewById(R.id.buttonScanner);
        record.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                startRecording();
            }
        });
        stabilize.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                // ����ͼ���ȶ�����
                Camera.Parameters parameters=camera.getParameters();
                if(parameters.isVideoStabilizationSupported())
                    parameters.setVideoStabilization(true);
                camera.setParameters(parameters);
            }
        });
        this.findViewById(R.id.buttonstop).setOnClickListener(new OnClickListener(){
            
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        scanner.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                mediaScan("/sdcard/myvideorecording.mp4");
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        camera=Camera.open();// ������ͷ
        Camera.Parameters parameters=camera.getParameters();
        parameters.setRecordingHint(true);// ����camera����������mediaRecorder����ʱ�������Ч��
        // if(parameters.isVideoStabilizationSupported())
        // parameters.setVideoStabilization(true);// ����camera�������ṩͼ���ȶ�����
        camera.setParameters(parameters);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mediaRecorder.reset();// ����mediaRecorder
        mediaRecorder.release();// �ͷ�mediaRecorder
        camera.lock();// ��������ͷ
        camera.release();// �ͷ�����ͷ
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder=holder;
        try{
            prepareVideoCamera();
        } catch(IllegalStateException e){
            Log.e(TAG, "Illegal State Exception", e);
        } catch(IOException e){
            Log.e(TAG, "I/O Exception", e);
        }
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.holder=null;
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    
    private void prepareVideoCamera() throws IllegalStateException, IOException {
        mediaRecorder=new MediaRecorder();
        // 1����������ͷ
        camera.unlock();
        // 2�� ������ͷ�����mediaRecorder
        mediaRecorder.setCamera(camera);
        // 3����������Դ
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// ��ƵԴ
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// ��ƵԴ
        // 4�����������ļ�
        CamcorderProfile profile=null;
        if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P))
            profile=CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
        else if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P))
            profile=CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
        else if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
            profile=CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        else if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH))
            profile=CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        if(profile != null)
            mediaRecorder.setProfile(profile);
        // 5���ƶ�����ļ�
        mediaRecorder.setOutputFile("/sdcard/myvideorecording.mp4");
        // 6������Ԥ��
        mediaRecorder.setPreviewDisplay(holder.getSurface());
        // 7��mediaRecorder׼�����
        mediaRecorder.prepare();
    }
    
    private void startRecording() {
        // ��ʼ¼��
        try{
            mediaRecorder.start();
        } catch(IllegalStateException e){
            mediaRecorder.release();
            camera.lock();
            Log.d(TAG, "Illegal State Exception", e);
        }
    }
    
    private void stopRecording() {
        // ֹͣ¼��
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.lock();// ��������ͷ
        try{
            // �س���׼��״̬
            prepareVideoCamera();
        } catch(IllegalStateException e){
            Log.e(TAG, "Illegal State Exception", e);
        } catch(IOException e){
            Log.e(TAG, "I/O Exception", e);
        }
    }
    
    private void mediaScan(final String filePath) {
        // ɨ����ļ���ý���
        MediaScannerConnectionClient mediaScannerClient=new MediaScannerConnectionClient(){
            
            private MediaScannerConnection msc=null;
            //
            {
                msc=new MediaScannerConnection(VideoCameraActivity.this, this);
                msc.connect();
            }
            
            public void onMediaScannerConnected() {
                String mimeType=null;
                msc.scanFile(filePath, mimeType);
            }
            
            public void onScanCompleted(String path, Uri uri) {
                msc.disconnect();
                Log.d(TAG, "File Added at: " + uri.toString());
            }
        };
        mediaScannerClient.onMediaScannerConnected();
    }
}