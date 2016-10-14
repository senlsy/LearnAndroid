package com.paad.surfaceview;

/**
 * Listing 11-8: Surface View skeleton implementation
 */
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    
    private SurfaceHolder holder;
    
    private MySurfaceViewThread mySurfaceViewThread;
    
    private boolean hasSurface;
    
    public MySurfaceView(Context context){
        super(context);
        init();
    }
    
    public MySurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    
    public MySurfaceView(Context context,
                         AttributeSet ats,
                         int defaultStyle){
        super(context, ats, defaultStyle);
        init();
    }
    
    private void init() {
        // 取出SurfaceView的成员变量SurfaceHodler，用hodler引用它，并为添加一个回调。
        holder=getHolder();
        holder.addCallback(this);
        hasSurface=false;
    }
    
    public void resume() {
        // Create and start the graphics update thread.
        if(mySurfaceViewThread == null){
            mySurfaceViewThread=new MySurfaceViewThread();
            if(hasSurface == true)
                mySurfaceViewThread.start();
        }
    }
    
    public void pause() {
        // Kill the graphics update thread
        if(mySurfaceViewThread != null){
            mySurfaceViewThread.requestExitAndWait();
            mySurfaceViewThread=null;
        }
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        //参数hodler与getHolder()得到的hodler是同一个对象
        hasSurface=true;
        if(mySurfaceViewThread != null)
            mySurfaceViewThread.start();
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface=false;
        pause();
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if(mySurfaceViewThread != null)
            mySurfaceViewThread.onWindowResize(w, h);
    }
    
    class MySurfaceViewThread extends Thread
    {
        
        private boolean done;
        
        MySurfaceViewThread(){
            super();
            done=false;
        }
        
        @Override
        public void run() {
            SurfaceHolder surfaceHolder=holder;
            // 循环绘画，直到线程标记为停止
            while( !done){
                // 锁定surface，并返回要在其上绘制的canvas
                Canvas canvas=surfaceHolder.lockCanvas();
                // TODO: 绘画
                // 绘画完成就解锁canvas，并post出去。
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        
        public void requestExitAndWait() {
            //把线程标记完成，并合并到主线程。
            done=true;
            try{
                join();
            } catch(InterruptedException ex){
            }
        }
        
        public void onWindowResize(int w, int h) {
            // 可以处理surface的尺寸
        }
    }
}