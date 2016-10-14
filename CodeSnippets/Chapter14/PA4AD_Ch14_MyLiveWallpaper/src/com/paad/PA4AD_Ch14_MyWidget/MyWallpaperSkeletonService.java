package com.paad.PA4AD_Ch14_MyWidget;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


//һ������LiveWallpaperʵ�ֵĿ��
public class MyWallpaperSkeletonService extends WallpaperService
{
    
    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperServiceEngine();
    }
    
    // һ��WallpaperService.Engine��ʵ�ֿ��
    public class MyWallpaperServiceEngine extends WallpaperService.Engine
    {
        
        private static final int FPS=30;
        private final Handler handler=new Handler();
        
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }
        
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep,
                                     int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
                                   xPixelOffset, yPixelOffset);
            // �û���������Ļ����������Ļ��ƫ����
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            // LiveWallpaper���յ���Ļ�����¼�����
        }
        
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            // SurfaceHolder������ɣ����Կ�ʼ���Ʋ���
            drawFrame();
        }
        
        private void drawFrame() {
            final SurfaceHolder holder=getSurfaceHolder();
            Canvas canvas=null;
            try{
                canvas=holder.lockCanvas();
                if(canvas != null){
                    // ���Ʋ���
                }
            } finally{
                if(canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawSurface);
            handler.postDelayed(drawSurface, 1000 / FPS);// ѭ������
        }
        
        private final Runnable drawSurface=new Runnable(){
            
            public void run() {
                drawFrame();
            }
        };
    }
}