package com.paad.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;


public class MyActivity extends Activity
{
    
    SensorManager sensorManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String service_name=Context.SENSOR_SERVICE;
        sensorManager=(SensorManager)getSystemService(service_name);
        // findScreenOrientation();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // registerProximitySensor();// 注册距离传感器
        // registerAccelerometer();//注册加速计传感器
        registerAccelerometerAndMagnetometer();// 磁感器和加速计传感器
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // sensorManager.unregisterListener(sensorListenner);
        unregisterAccelerometerAndMagnetometer();
    }
    
    SensorEventListener sensorListenner=new SensorEventListener(){
        
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values=event.values;
            switch(event.sensor.getType()){
                case Sensor.TYPE_PROXIMITY:
                    Log.i("lintest", "距离传感器:" + values[0]);
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    Log.i("lintest", "加速计传感器:x轴=" + values[0] + ",y轴=" + values[1] + ",z轴=" + values[2]);
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            String accuracyStr=null;
            switch(accuracy){
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    accuracyStr="SENSOR_STATUS_ACCURACY_HIGH";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    accuracyStr="SENSOR_STATUS_ACCURACY_MEDIUM";
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    accuracyStr="SENSOR_STATUS_ACCURACY_LOW";
                    break;
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    accuracyStr="SENSOR_STATUS_UNRELIABLE";
                    break;
                default:
                    break;
            }
            switch(sensor.getType()){
                case Sensor.TYPE_PROXIMITY:
                    Log.i("lintest", "距离传感器精度发生变化:" + accuracyStr);
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    Log.i("lintest", "加速计传感器精度发生变化:" + accuracyStr);
                    break;
                default:
                    break;
            }
        }
    };
    
    private void registerProximitySensor() {
        // 距离传感器
        Sensor sensor=sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(sensorListenner, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    private void findScreenOrientation() {
        // 当前屏幕旋转方向
        String windowSrvc=Context.WINDOW_SERVICE;
        WindowManager wm=((WindowManager)getSystemService(windowSrvc));
        Display display=wm.getDefaultDisplay();
        int rotation=display.getRotation();
        switch(rotation){
            case (Surface.ROTATION_0):
                Log.i("lintest", "旋转0度");
                break; // Natural
            case (Surface.ROTATION_90):
                Log.i("lintest", "旋转90度");
                break; // On its left side
            case (Surface.ROTATION_180):
                Log.i("lintest", "旋转180度");
                break; // Updside down
            case (Surface.ROTATION_270):
                Log.i("lintest", "旋转270度");
                break; // On its right side
            default:
                break;
        }
    }
    
    private void registerAccelerometer() {
        // 加速计传感器
        int sensorType=Sensor.TYPE_ACCELEROMETER;
        Sensor sensor=sensorManager.getDefaultSensor(sensorType);
        if(sensor != null)
            sensorManager.registerListener(sensorListenner, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    // 利用磁感器跟加速计找出方向------------------
    private float[] accelerometerValues;
    
    private float[] magneticFieldValues;
    
    final SensorEventListener myAccelerometerListener=new SensorEventListener(){
        
        // 加速计监听器
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues=sensorEvent.values;
            calculateOrientation();
        }
        
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    
    final SensorEventListener myMagneticFieldListener=new SensorEventListener(){
        
        // 磁感器监听器
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues=sensorEvent.values;
            calculateOrientation();
        }
        
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    
    private void registerAccelerometerAndMagnetometer() {
        Sensor aSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mfSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(myAccelerometerListener,
                                       aSensor,
                                       SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(myMagneticFieldListener,
                                       mfSensor,
                                       SensorManager.SENSOR_DELAY_UI);
    }
    
    private void unregisterAccelerometerAndMagnetometer()
    {
        sensorManager.unregisterListener(myAccelerometerListener);
        sensorManager.unregisterListener(myMagneticFieldListener);
    }
    
    private void calculateOrientation() {
        if(accelerometerValues==null||magneticFieldValues==null)
            return;
        // 利用磁感器跟加速计传感器找出当前方向
        float[] values=new float[3];  
        float[] R=new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);// 此处获得的是旋转的弧度
        values[0]=(float)Math.toDegrees(values[0]); // 弧度换算成角度，与z轴的旋转角度
        values[1]=(float)Math.toDegrees(values[1]); // 与x轴的旋转角度
        values[2]=(float)Math.toDegrees(values[2]); // 与y轴的旋转角度
        Log.e("lintest", "x轴旋转角度=" + values[1] + ",y轴旋转角度=" + values[2] + ",z轴旋转角度=" + values[0]);
    }
    
    private void calculateRemappedOrientation() {
        float[] inR=new float[9];
        float[] outR=new float[9];
        float[] values=new float[3];
        String windoSrvc=Context.WINDOW_SERVICE;
        WindowManager wm=((WindowManager)getSystemService(windoSrvc));
        Display display=wm.getDefaultDisplay();
        int rotation=display.getRotation();
        int x_axis=SensorManager.AXIS_X;
        int y_axis=SensorManager.AXIS_Y;
        switch(rotation){
            case (Surface.ROTATION_0):
                break;
            case (Surface.ROTATION_90):
                x_axis=SensorManager.AXIS_Y;
                y_axis=SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_180):
                y_axis=SensorManager.AXIS_MINUS_Y;
                break;
            case (Surface.ROTATION_270):
                x_axis=SensorManager.AXIS_MINUS_Y;
                y_axis=SensorManager.AXIS_X;
                break;
            default:
                break;
        }
        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
        SensorManager.getOrientation(outR, values);
    }
    
    private void deprecatedSensorListener() {
        SensorManager sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        int sensorType=Sensor.TYPE_ORIENTATION;
        sm.registerListener(myOrientationListener, sm.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    final SensorEventListener myOrientationListener=new SensorEventListener(){
        
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
                float headingAngle=sensorEvent.values[0];
                float pitchAngle=sensorEvent.values[1];
                float rollAngle=sensorEvent.values[2];
            }
        }
        
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    
    /**
     * Listing 12-9: Calculating an orientation change using the gyroscope
     * Sensor
     */
    final float nanosecondsPerSecond=1.0f / 1000000000.0f;
    
    private long lastTime=0;
    
    final float[] angle=new float[3];
    
    SensorEventListener myGyroListener=new SensorEventListener(){
        
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(lastTime != 0){
                final float dT=(sensorEvent.timestamp - lastTime) *
                               nanosecondsPerSecond;
                angle[0]+=sensorEvent.values[0] * dT;
                angle[1]+=sensorEvent.values[1] * dT;
                angle[2]+=sensorEvent.values[2] * dT;
            }
            lastTime=sensorEvent.timestamp;
        }
        
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    
    private void registerGyro() {
        SensorManager sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        int sensorType=Sensor.TYPE_GYROSCOPE;
        sm.registerListener(myGyroListener,
                            sm.getDefaultSensor(sensorType),
                            SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    private void calculatingAltitude() {
        final SensorEventListener myPressureListener=new SensorEventListener(){
            
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
                    float currentPressure=sensorEvent.values[0];
                    float altitude=SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, currentPressure);
                }
            }
            
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        SensorManager sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        int sensorType=Sensor.TYPE_PRESSURE;
        sm.registerListener(myPressureListener,
                            sm.getDefaultSensor(sensorType),
                            SensorManager.SENSOR_DELAY_NORMAL);
    }
}