/**   
 * @Title: f.java
 * @Package com.paad.surfaceview
 * @Description: TODO
 * @author LinSQ
 * @date 2015-1-26 ����10:56:56
 * @version V1.0   
 */
package com.paad.surfaceview;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class TouchActivity extends Activity
{
    
    private static final int NONE=0;
    
    private static final int MOVE=1;
    
    private static final int ZOOM=2;
    
    private static final int ROTATION=1;
    
    private int mode=NONE;
    
    private Matrix matrix=new Matrix();
    
    private Matrix savedMatrix=new Matrix();
    
    private PointF start=new PointF();
    
    private PointF mid=new PointF();
    
    private float s=0;
    
    private float oldDistance;
    
    private int rotate=NONE;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image4scale);
        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setOnTouchListener(new OnTouchListener()
        {
            
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                ImageView imageView=(ImageView)view;
                Log.i("lintest", ""+(event.getAction()&MotionEvent.ACTION_POINTER_ID_MASK));
                switch(event.getAction() & MotionEvent.ACTION_MASK){// �������ֳ������¼�
                    case MotionEvent.ACTION_DOWN:// ���㴥��down
                        Log.e("lintest", "���㴥��---ACTION_DOWN");
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode=MOVE;
                        rotate=NONE;
                        break;
                    case MotionEvent.ACTION_UP:// ���㴥��up
                        mode=NONE;
                        Log.e("lintest", "���㴥��---ACTION_UP");
                        break;
                    case MotionEvent.ACTION_POINTER_UP:// ��㴥��up
                        mode=NONE;
                        Log.e("lintest", "��㴥��---ACTION_UP");
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:// ��㴥��down
                        Log.e("lintest", "��㴥��---ACTION_POINTER_DOWN");
                        // event.getX(0)��ָ0���x����
                        // event.getX(1)��ָ1���x����
                        oldDistance=(float)Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) + (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));
                        if(oldDistance > 10f){
                            savedMatrix.set(matrix);
                            mid.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
                            mode=ZOOM;
                        }
                    case MotionEvent.ACTION_MOVE:// ������߶���move�¼���һ���ġ�
                        if(mode == MOVE)
                        {
                            // ���㴥�ص�����
                            if(rotate == NONE){
                                savedMatrix.set(matrix);
                                mid.set(event.getX(), event.getY());
                                rotate=ROTATION;
                            }
                            else{
                                matrix.set(savedMatrix);
                                double a=Math.atan((mid.y - start.y) / (mid.x - start.x));
                                double b=Math.atan((event.getY() - mid.y) / (event.getX() - mid.x));
                                if((b - a < Math.PI / 2 && b - a > Math.PI / 18) || ((b + Math.PI) % Math.PI - a < Math.PI / 2 && (b + Math.PI) % Math.PI - a > Math.PI / 18)){
                                    matrix.postScale((float)0.9, (float)0.9);
                                }
                                else if((a - b < Math.PI / 2 && a - b > Math.PI / 18) || ((a + Math.PI) % Math.PI - b < Math.PI / 2 && (a + Math.PI) % Math.PI - b > Math.PI / 18)){
                                    matrix.postScale((float)1.1, (float)1.1);
                                }
                                start.set(event.getX(), event.getY());
                                rotate=NONE;
                            }
                        }
                        else if(mode == ZOOM)
                        {// ��㴥�ص�����
                            float newDistance;
                            newDistance=(float)Math.sqrt((event.getX(0) - event.getX(1)) * (event.getX(0) - event.getX(1)) + (event.getY(0) - event.getY(1)) * (event.getY(0) - event.getY(1)));
                            if(newDistance > 10f){
                                matrix.set(savedMatrix);
                                matrix.postScale(newDistance / oldDistance, newDistance / oldDistance, mid.x, mid.y);
                                oldDistance=newDistance;
                                savedMatrix.set(matrix);
                            }
                        }
                        break;
                }
                imageView.setImageMatrix(matrix);
                return true;
            }
        });
    }
}