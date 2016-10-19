package com.yuexunit.android.library.library_utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


/**
 * @ClassName: WindInfoUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author LinSQ
 * @date 2015-3-13 下午5:49:32
 * @version 1.0
 * @note
 */
public class WindInfoUtil
{
    
    /**
     * @Description: 获取屏幕信息
     * @param @param context 设定文件
     * @return void 返回类型
     * @throws
     * @version 1.0
     * @note 获取屏幕的信息，是经过系统调整过后的屏幕信息（例如系统增加了虚拟导航栏，屏幕高度就扣除了虚拟导航栏的高度）
     */
    public static String outputWindInfo(Context context)
    {
        WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.toString();
        // Log.i("lintest", "w=" + outMetrics.widthPixels
        // + "\n h=" + outMetrics.heightPixels// 不包括虚拟导航栏的高度
        // + "\n density=" + outMetrics.density
        // + "\n densityDpi=" + outMetrics.densityDpi
        // + "\n scaledDensity=" + outMetrics.scaledDensity
        // + "\n xdpi" + outMetrics.xdpi
        // + "\n ydpi" + outMetrics.ydpi);
        //
        // DisplayMetrics metrics=context.getResources().getDisplayMetrics();
        // Log.w("lintest", "w=" + metrics.widthPixels
        // + "\nh=" + metrics.heightPixels
        // + "\ndensity=" + metrics.density
        // + "\ndensityDpi=" + metrics.densityDpi
        // + "\nscaledDensity=" + metrics.scaledDensity
        // + "\nxdpi" + metrics.xdpi
        // + "\nydpi" + metrics.ydpi);
    }
    
    public static Point getScreenWH(Context context)
    {
        WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        Point info=new Point();
        if(Build.VERSION.SDK_INT >= 9)
            display.getSize(info);
        else
            info=new Point(display.getWidth(), display.getHeight());
        return info;
    }
}
