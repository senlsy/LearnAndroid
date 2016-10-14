/**   
 * @Title: TouchSurfaceView.java
 * @Package com.paad.surfaceview
 * @Description: TODO
 * @author LinSQ
 * @date 2015-1-26 上午9:59:16
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
 * @date 2015-1-26 上午9:59:16
 * 
 */
public class TouchSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    
    private static final int MAX_TOUCHPOINTS=10;
    private static final String START_TEXT="请随便触摸屏幕进行测试";
    private Paint textPaint=new Paint();
    private Paint touchPaints[]=new Paint[MAX_TOUCHPOINTS];// 每一个触摸事件对应一个画笔
    private int colors[]=new int[MAX_TOUCHPOINTS];// 每个画笔对应得颜色
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
        setFocusable(true); // 在非触摸模式下可以获取焦点
        setFocusableInTouchMode(true); // 在触摸模式下也可以获取焦点
        init();
    }
    
    private void init() {
        // 初始化10个不同颜色的画笔
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
    
    /* 处理触屏事件 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获得屏幕触点数量
        int pointerCount=event.getPointerCount();
        if(pointerCount > MAX_TOUCHPOINTS){
            pointerCount=MAX_TOUCHPOINTS;
        }
        // 锁定Canvas,开始进行相应的界面绘制
        Canvas c=getHolder().lockCanvas();
        //
        int action=event.getActionMasked();// 事件类型。
        int mActivePointerId=event.getPointerId(0);// 0,第一根手指的事件id指针
        int pointerIndex=event.findPointerIndex(mActivePointerId);//这个事件id存的坐标位置。
        //
        Log.e("lintest", "-----------------");
        if(action == MotionEvent.ACTION_DOWN)
        {// 不管单点触控还是多点触控，第一根手点下指触发的肯定是ACTION_DOWN。
            Log.e("lintest", "ACTION_DOWN event=" + event);
        }
        if(action == MotionEvent.ACTION_POINTER_DOWN)
        {// 在多点触控下，第二个手指或者其余手指点下后触发ACTION_POINTER_DOWN
            Log.e("lintest", "ACTION_POINTER_DOWN event=" + event);
        }
        // 不管单点还是多点触控，移动的时候单点和多点触控使用是同一个ACTION_MOVE
        if(action == MotionEvent.ACTION_POINTER_UP)
        {// 在多点触控下，第二个手指或者其余手指抬起后触发ACTION_POINTER_UP
            Log.e("lintest", "ACTION_POINTER_UP event=" + event);
        }
        if(action == MotionEvent.ACTION_UP){
            // 不管单点还是多点触控，最后一个手指抬起肯定触发ACTION_UP
            Log.e("lintest", "ACTION_UP event=" + event);
        }
        Log.e("lintest", "-----------------");
        //
        if(c != null){
            c.drawColor(Color.BLACK);// 清屏幕
            if(event.getAction() == MotionEvent.ACTION_UP){
                // 当手离开屏幕时，清屏
                Log.e("lintest", "ACTION_UP event=" + event);
            }
            else{
                // 先在屏幕上画一个十字，然后画一个圆
                for(int i=0;i < pointerCount;i++){
                    // 获取每一个触点的坐标，然后开始绘制
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
            // 画完后，unlock
            getHolder().unlockCanvasAndPost(c);
        }
        return true;
    }
    
    /**
     * 
     * @Title: drawCrosshairsAndText
     * @Description: TODO
     * @param @param x坐标
     * @param @param y坐标
     * @param @param paint画笔
     * @param @param ptr
     * @param @param id
     * @param @param c操作
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
     * 画圆
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
