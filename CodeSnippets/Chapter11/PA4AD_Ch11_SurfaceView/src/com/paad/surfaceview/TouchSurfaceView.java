/**   
 * @Title: TouchSurfaceView.java
 * @Package com.paad.surfaceview
 * @Description: TODO
 * @author LinSQ
 * @date 2015-1-26 ����9:59:16
 * @version V1.0   
 */
package com.paad.surfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * @ClassName: TouchSurfaceView
 * @Description: TODO
 * @author LinSQ
 * @date 2015-1-26 ����9:59:16
 * 
 */
public class TouchSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    
    private static final int MAX_TOUCHPOINTS=10;
    private static final String START_TEXT="����㴥����Ļ���в���";
    private Paint textPaint=new Paint();
    private Paint touchPaints[]=new Paint[MAX_TOUCHPOINTS];// ÿһ�������¼���Ӧһ������
    private int colors[]=new int[MAX_TOUCHPOINTS];// ÿ�����ʶ�Ӧ����ɫ
    private int width, height, textHeight;
    
    public TouchSurfaceView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initHolder();
    }
    
    public TouchSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        initHolder();
    }
    
    public TouchSurfaceView(Context context){
        super(context);
        initHolder();
    }
    
    private void initHolder()
    {
        SurfaceHolder holder=getHolder();
        holder.addCallback(this);
        setFocusable(true); // �ڷǴ���ģʽ�¿��Ի�ȡ����
        setFocusableInTouchMode(true); // �ڴ���ģʽ��Ҳ���Ի�ȡ����
        init();
    }
    
    private void init() {
        // ��ʼ��10����ͬ��ɫ�Ļ���
        textPaint.setColor(Color.WHITE);
        colors[0]=Color.BLUE;
        colors[1]=Color.RED;
        colors[2]=Color.GREEN;
        colors[3]=Color.YELLOW;
        colors[4]=Color.CYAN;
        colors[5]=Color.MAGENTA;
        colors[6]=Color.DKGRAY;
        colors[7]=Color.WHITE;
        colors[8]=Color.LTGRAY;
        colors[9]=Color.GRAY;
        for(int i=0;i < MAX_TOUCHPOINTS;i++){
            touchPaints[i]=new Paint();
            touchPaints[i].setColor(colors[i]);
        }
    }
    
    /* �������¼� */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // �����Ļ��������
        int pointerCount=event.getPointerCount();
        if(pointerCount > MAX_TOUCHPOINTS){
            pointerCount=MAX_TOUCHPOINTS;
        }
        // ����Canvas,��ʼ������Ӧ�Ľ������
        Canvas c=getHolder().lockCanvas();
        //
        int action=event.getActionMasked();// �¼����͡�
        int mActivePointerId=event.getPointerId(0);// 0,��һ����ָ���¼�idָ��
        int pointerIndex=event.findPointerIndex(mActivePointerId);//����¼�id�������λ�á�
        //
        Log.e("lintest", "-----------------");
        if(action == MotionEvent.ACTION_DOWN)
        {// ���ܵ��㴥�ػ��Ƕ�㴥�أ���һ���ֵ���ָ�����Ŀ϶���ACTION_DOWN��
            Log.e("lintest", "ACTION_DOWN event=" + event);
        }
        if(action == MotionEvent.ACTION_POINTER_DOWN)
        {// �ڶ�㴥���£��ڶ�����ָ����������ָ���º󴥷�ACTION_POINTER_DOWN
            Log.e("lintest", "ACTION_POINTER_DOWN event=" + event);
        }
        // ���ܵ��㻹�Ƕ�㴥�أ��ƶ���ʱ�򵥵�Ͷ�㴥��ʹ����ͬһ��ACTION_MOVE
        if(action == MotionEvent.ACTION_POINTER_UP)
        {// �ڶ�㴥���£��ڶ�����ָ����������ָ̧��󴥷�ACTION_POINTER_UP
            Log.e("lintest", "ACTION_POINTER_UP event=" + event);
        }
        if(action == MotionEvent.ACTION_UP){
            // ���ܵ��㻹�Ƕ�㴥�أ����һ����ָ̧��϶�����ACTION_UP
            Log.e("lintest", "ACTION_UP event=" + event);
        }
        Log.e("lintest", "-----------------");
        //
        if(c != null){
            c.drawColor(Color.BLACK);// ����Ļ
            if(event.getAction() == MotionEvent.ACTION_UP){
                // �����뿪��Ļʱ������
                Log.e("lintest", "ACTION_UP event=" + event);
            }
            else{
                // ������Ļ�ϻ�һ��ʮ�֣�Ȼ��һ��Բ
                for(int i=0;i < pointerCount;i++){
                    // ��ȡÿһ����������꣬Ȼ��ʼ����
                    int id=event.getPointerId(i);
                    int x=(int)event.getX(i);
                    int y=(int)event.getY(i);
                    drawCrosshairsAndText(x, y, touchPaints[id], i, id, c);
                }
                for(int i=0;i < pointerCount;i++){
                    int id=event.getPointerId(i);
                    int x=(int)event.getX(i);
                    int y=(int)event.getY(i);
                    drawCircle(x, y, touchPaints[id], c);
                }
            }
            // �����unlock
            getHolder().unlockCanvasAndPost(c);
        }
        return true;
    }
    
    /**
     * 
     * @Title: drawCrosshairsAndText
     * @Description: TODO
     * @param @param x����
     * @param @param y����
     * @param @param paint����
     * @param @param ptr
     * @param @param id
     * @param @param c����
     * @return void
     * @throws
     */
    private void drawCrosshairsAndText(int x, int y, Paint paint, int ptr, int pointerID, Canvas canvas) {
        canvas.drawLine(0, y, width, y, paint);
        canvas.drawLine(x, 0, x, height, paint);
        int textY=(int)(50 + textHeight * ptr);
        canvas.drawText("x" + ptr + "=" + x, 10, textY, textPaint);
        canvas.drawText("y" + ptr + "=" + y, 100, textY, textPaint);
        canvas.drawText("id" + ptr + "=" + pointerID, width - 55, textY, textPaint);
    }
    
    /**
     * ��Բ
     */
    private void drawCircle(int x, int y, Paint paint, Canvas c) {
        c.drawCircle(x, y, 40, paint);
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("lintest", "surfaceChanged():width=" + width + ",height=" + height);
        this.width=width;
        this.height=height;
        textPaint.setTextSize(20);
        textHeight=(int)textPaint.measureText("yY");
        Canvas c=getHolder().lockCanvas();
        if(c != null){
            c.drawColor(Color.BLACK);
            float tWidth=textPaint.measureText(START_TEXT);
            c.drawText(START_TEXT, width / 2 - tWidth / 2, height / 2, textPaint);
            getHolder().unlockCanvasAndPost(c);
        }
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
}
