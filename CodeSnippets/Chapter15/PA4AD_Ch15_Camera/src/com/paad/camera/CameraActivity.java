package com.paad.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * Listing 15-26: Previewing a real-time camera stream
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback
{
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SurfaceView surface=(SurfaceView)findViewById(R.id.surfaceView);
        SurfaceHolder holder=surface.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(400, 300);
        Button snap=(Button)findViewById(R.id.buttonTakePicture);
        Button exif=(Button)findViewById(R.id.buttonExif);
        Button distances=(Button)findViewById(R.id.buttonFocusDistances);
        snap.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                takePicture();
            }
        });
        exif.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                modifyExif();
            }
        });
        distances.setOnClickListener(new OnClickListener(){
            
            public void onClick(View v) {
                findFocusDistances();
            }
        });
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            camera.setPreviewDisplay(holder);// ������ʾ����ͷԤ��
            camera.setPreviewCallback(new PreviewCallback(){
                
                // ����ÿһ��Ԥ��֡�Ļص�
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    int quality=60;
                    Size previewSiez=camera.getParameters().getPreviewSize();
                    YuvImage image=new YuvImage(data, ImageFormat.NV21, previewSiez.width, previewSiez.height, null);
                    ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, previewSiez.width, previewSiez.height), quality, outputStream);
                    // Ȼ���ͼ����в���
                }
            });
            camera.startPreview();// ��������ͷԤ��
            camera.startFaceDetection();// ���ü��͸�������
        } catch(IOException e){
            Log.d(TAG, e.getMessage());
        }
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopFaceDetection();// ֹͣ���͸�������
        camera.stopPreview();// ֹͣ����ͷ
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        camera=Camera.open();
    }
    
    private static final String TAG="CameraActivity";
    private Camera camera;
    
    private void findFocusDistances() {
        // �ҳ�����
        Camera.Parameters parameters=camera.getParameters();
        float[] focusDistances=new float[3];
        parameters.getFocusDistances(focusDistances);
        float near=focusDistances[Camera.Parameters.FOCUS_DISTANCE_NEAR_INDEX];
        float far=focusDistances[Camera.Parameters.FOCUS_DISTANCE_FAR_INDEX];
        float optimal=focusDistances[Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX];
        Log.d(TAG, "Focus Distances: " + near + ", " + far + ", " + optimal);
    }
    
    private void takePicture() {
        // ����
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    ShutterCallback shutterCallback=new ShutterCallback(){
        
        // ����ʱ���Ž����ص�
        public void onShutter() {
        }
    };
    PictureCallback rawCallback=new PictureCallback(){
        
        // ����ʱ������rawͼ��ص�
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };
    PictureCallback jpegCallback=new PictureCallback(){
        
        // ����ʱ������jpegͼ��ص�
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream=null;
            try{
                String path=Environment.getExternalStorageDirectory()
                            + "\test.jpg";
                outStream=new FileOutputStream(path);
                outStream.write(data);
                outStream.close();
            } catch(FileNotFoundException e){
                Log.e(TAG, "File Note Found", e);
            } catch(IOException e){
                Log.e(TAG, "IO Exception", e);
            }
        }
    };
    
    private void modifyExif() {
        // ��jpegͼƬд��һЩ������Ϣ
        File file=new File(Environment.getExternalStorageDirectory(),
                           "test.jpg");
        try{
            ExifInterface exif=new ExifInterface(file.getCanonicalPath());
            String model=exif.getAttribute(ExifInterface.TAG_MODEL);
            Log.d(TAG, "Model: " + model);
            exif.setAttribute(ExifInterface.TAG_MAKE, "My Phone");
        } catch(IOException e){
            Log.e(TAG, "IO Exception", e);
        }
    }
}