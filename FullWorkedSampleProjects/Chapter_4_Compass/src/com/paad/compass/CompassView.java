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
        // Բ�λ���-----------
        circlePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        // ����-------------
        northString="N";
        eastString="E";
        southString="S";
        westString="W";
        // ���ֱ�-------------------
        textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_color));
        textHeight=(int)textPaint.measureText("yY");// �û���������ĸ߶�
        // �̶ȱ�----------------------
        markerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.marker_color));
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // ���ô�С
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
            // ûָ��ֵ�ͷ���Ĭ��200��С
            result=200;
        }
        else{
            // ��Ϊ����ϣ���������ռ�
            // ���Է��ظ�view�����ŵ�ʵ�ʴ�С����
            result=specSize;
        }
        return result;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        // �ҳ���̱���ΪԲ�̵İ뾶
        int mMeasuredWidth=getMeasuredWidth();
        int mMeasuredHeight=getMeasuredHeight();
        int px=mMeasuredWidth / 2;
        int py=mMeasuredHeight / 2;
        int radius=Math.min(px, py);
        // ��������Բ��
        canvas.drawCircle(px, py, radius, circlePaint);
        // ��Ϊ���̻���ת���Ƿ����ָ��̶ȡ�����Բ�̱���ѭת�����������ʾʱӦ�ûع�����ԭʼ����ϵ
        canvas.save();
        // ��תbearing�ĽǶȣ���ʼ���ָ��̶�
        canvas.rotate( -bearing, px, py);
        // д�ֵ�λ��-----------
        int textWidth=(int)textPaint.measureText("W");
        int cardinalX=px - textWidth / 2;
        int cardinalY=py - radius + textHeight;
        // ÿ��15�Ȼ�һ���̶� andÿ��45��дһ����������.
        for(int i=0;i < 24;i++){
            // Draw a marker���Ͽ̶�.
            canvas.drawLine(px, py - radius, px, py - radius + 10, markerPaint);
            // ���浱ǰλ�ã�����һ���ָ߶�
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
            // �ع�������һ����ǰ������ϵ
            canvas.restore();
            // ��ת15��
            canvas.rotate(15, px, py);
        }
        canvas.restore();// �ع���ԭʼ����ϵλ��
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
