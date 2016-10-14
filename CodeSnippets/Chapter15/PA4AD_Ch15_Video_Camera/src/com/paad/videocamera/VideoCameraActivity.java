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
                // 设置图像稳定能力
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
        camera=Camera.open();// 打开摄像头
        Camera.Parameters parameters=camera.getParameters();
        parameters.setRecordingHint(true);// 设置camera参数，缩短mediaRecorder启动时间来提高效率
        // if(parameters.isVideoStabilizationSupported())
        // parameters.setVideoStabilization(true);// 设置camera参数，提供图像稳定能力
        camera.setParameters(parameters);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mediaRecorder.reset();// 重置mediaRecorder
        mediaRecorder.release();// 释放mediaRecorder
        camera.lock();// 锁定摄像头
        camera.release();// 释放摄像头
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
        // 1，解锁摄像头
        camera.unlock();
        // 2， 将摄像头分配给mediaRecorder
        mediaRecorder.setCamera(camera);
        // 3，设置输入源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 音频源
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
        // 4，设置配置文件
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
        // 5，制定输出文件
        mediaRecorder.setOutputFile("/sdcard/myvideorecording.mp4");
        // 6，设置预览
        mediaRecorder.setPreviewDisplay(holder.getSurface());
        // 7，mediaRecorder准备完成
        mediaRecorder.prepare();
    }
    
    private void startRecording() {
        // 开始录制
        try{
            mediaRecorder.start();
        } catch(IllegalStateException e){
            mediaRecorder.release();
            camera.lock();
            Log.d(TAG, "Illegal State Exception", e);
        }
    }
    
    private void stopRecording() {
        // 停止录制
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.lock();// 锁定摄像头
        try{
            // 重出与准备状态
            prepareVideoCamera();
        } catch(IllegalStateException e){
            Log.e(TAG, "Illegal State Exception", e);
        } catch(IOException e){
            Log.e(TAG, "I/O Exception", e);
        }
    }
    
    private void mediaScan(final String filePath) {
        // 扫描该文件进媒体库
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