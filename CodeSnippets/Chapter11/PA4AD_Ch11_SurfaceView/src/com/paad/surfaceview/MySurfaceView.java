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
        // ȡ��SurfaceView�ĳ�Ա����SurfaceHodler����hodler����������Ϊ���һ���ص���
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
        //����hodler��getHolder()�õ���hodler��ͬһ������
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
            // ѭ���滭��ֱ���̱߳��Ϊֹͣ
            while( !done){
                // ����surface��������Ҫ�����ϻ��Ƶ�canvas
                Canvas canvas=surfaceHolder.lockCanvas();
                // TODO: �滭
                // �滭��ɾͽ���canvas����post��ȥ��
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        
        public void requestExitAndWait() {
            //���̱߳����ɣ����ϲ������̡߳�
            done=true;
            try{
                join();
            } catch(InterruptedException ex){
            }
        }
        
        public void onWindowResize(int w, int h) {
            // ���Դ���surface�ĳߴ�
        }
    }
}