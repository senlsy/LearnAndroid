package com.paad.compass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;


public class CompassView extends View
{
    
    private float bearing;
    
    public void setBearing(float _bearing) {
        bearing=_bearing;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }
    
    public float getBearing() {
        return bearing;
    }
    
    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private String northString;
    private String eastString;
    private String southString;
    private String westString;
    private int textHeight;
    
    public CompassView(Context context){
        super(context);
        initCompassView();
    }
    
    public CompassView(Context context, AttributeSet attrs){
        super(context, attrs);
        initCompassView();
    }
    
    public CompassView(Context context,
                       AttributeSet ats,
                       int defaultStyle){
        super(context, ats, defaultStyle);
        initCompassView();
    }
    
    protected void initCompassView() {
        setFocusable(true);
        Resources r=this.getResources();
        // 圆形画笔-----------
        circlePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 文字-------------
        northString="N";
        eastString="E";
        southString="S";
        westString="W";
        // 文字笔-------------------
        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_color));
        textHeight=(int)textPaint.measureText("yY");// 该画笔下字体的高度
        // 刻度笔----------------------
        markerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.marker_color));
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置大小
        int measuredWidth=measure(widthMeasureSpec);
        int measuredHeight=measure(heightMeasureSpec);
        int d=Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(d, d);
    }
    
    private int measure(int measureSpec) {
        int result=0;
        int specMode=MeasureSpec.getMode(measureSpec);
        int specSize=MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.UNSPECIFIED){
            // Return a default size of 200 if no bounds are specified.
            // 没指定值就返回默认200大小
            result=200;
        }
        else{
            // 因为我们希望填充这个空间
            // 所以返回父view给安排的实际大小即可
            result=specSize;
        }
        return result;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // 找出最短边作为圆盘的半径
        int mMeasuredWidth=getMeasuredWidth();
        int mMeasuredHeight=getMeasuredHeight();
        int px=mMeasuredWidth / 2;
        int py=mMeasuredHeight / 2;
        int radius=Math.min(px, py);
        // 画出背景圆盘
        canvas.drawCircle(px, py, radius, circlePaint);
        // 因为罗盘会旋转的是方向字跟刻度。背景圆盘本身不循转，所以最后显示时应该回滚至最原始坐标系
        canvas.save();
        // 旋转bearing的角度，开始画字跟刻度
        canvas.rotate( -bearing, px, py);
        // 写字的位置-----------
        int textWidth=(int)textPaint.measureText("W");
        int cardinalX=px - textWidth / 2;
        int cardinalY=py - radius + textHeight;
        // 每隔15度画一个刻度 and每隔45度写一个方向文字.
        for(int i=0;i < 24;i++){
            // Draw a marker画上刻度.
            canvas.drawLine(px, py - radius, px, py - radius + 10, markerPaint);
            // 保存当前位置，下移一个字高度
            canvas.save();
            canvas.translate(0, textHeight);
            // Draw the cardinal points
            if(i % 6 == 0){
                String dirString="";
                switch(i){
                    case (0):
                    {
                        dirString=northString;
                        int arrowY=2 * textHeight;
                        canvas.drawLine(px, arrowY, px - 5, 3 * textHeight,
                                        markerPaint);
                        canvas.drawLine(px, arrowY, px + 5, 3 * textHeight,
                                        markerPaint);
                        break;
                    }
                    case (6):
                        dirString=eastString;
                        break;
                    case (12):
                        dirString=southString;
                        break;
                    case (18):
                        dirString=westString;
                        break;
                }
                canvas.drawText(dirString, cardinalX, cardinalY, textPaint);
            }
            else if(i % 3 == 0){
                // Draw the text every alternate 45deg
                String angle=String.valueOf(i * 15);
                float angleTextWidth=textPaint.measureText(angle);
                int angleTextX=(int)(px - angleTextWidth / 2);
                int angleTextY=py - radius + textHeight;
                canvas.drawText(angle, angleTextX, angleTextY, textPaint);
            }
            // 回滚至下移一个字前的坐标系
            canvas.restore();
            // 旋转15度
            canvas.rotate(15, px, py);
        }
        canvas.restore();// 回滚最原始坐标系位置
    }
    
    @Override
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
        super.dispatchPopulateAccessibilityEvent(event);
        if(isShown()){
            String bearingStr=String.valueOf(bearing);
            if(bearingStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH)
                bearingStr=bearingStr.substring(0, AccessibilityEvent.MAX_TEXT_LENGTH);
            event.getText().add(bearingStr);
            return true;
        }
        else
            return false;
    }
}
