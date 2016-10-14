package com.paad.PA4AD_Ch14_MyWidget;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


//一个基本LiveWallpaper实现的框架
public class MyWallpaperSkeletonService extends WallpaperService
{
    
    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperServiceEngine();
    }
    
    // 一个WallpaperService.Engine的实现框架
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
            // 用户滑动主屏幕触发，主屏幕的偏移量
        }
        
        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            // LiveWallpaper接收到屏幕触摸事件触发
        }
        
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            // SurfaceHolder创建完成，可以开始绘制操作
            drawFrame();
        }
        
        private void drawFrame() {
            final SurfaceHolder holder=getSurfaceHolder();
            Canvas canvas=null;
            try{
                canvas=holder.lockCanvas();
                if(canvas != null){
                    // 绘制操作
                }
            } finally{
                if(canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawSurface);
            handler.postDelayed(drawSurface, 1000 / FPS);// 循环绘制
        }
        
        private final Runnable drawSurface=new Runnable(){
            
            public void run() {
                drawFrame();
            }
        };
    }
}