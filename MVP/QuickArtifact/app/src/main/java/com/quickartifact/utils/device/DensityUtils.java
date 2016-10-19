package com.quickartifact.utils.device;

import com.quickartifact.BaseApplication;

/**
 * Description: 单位转换的工具类
 *
 * @author liuranchao
 * @date 16/3/17 上午10:56
 */
public final class DensityUtils {

    /**
     * dp to px
     */
    private static final float RATIO = 0.5f;

    private DensityUtils() {

    }

    /**
     * Description: 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue dp
     * @return px
     */
    public static int dip2px(float dpValue) {
        final float scale = getDensity();
        return (int) (dpValue * scale + RATIO);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = getScaleDensity();
        return (int) (spValue * fontScale + RATIO);
    }

    /**
     * Description: 获取当前设备的像素
     * 0.75 - ldpi
     * 1.0 - mdpi
     * 1.5 - hdpi
     * 2.0 - xhdpi
     * 3.0 - xxhdpi
     * 4.0 - xxxhdpi
     */
    public static float getDensity() {
        return BaseApplication.getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * 获取当前设备缩放因子(px只能以1px最小单位存在)
     * 1dp : 0.75px
     * 1dp : 1.0px
     * 1dp : 1.5px
     * 1dp : 2.0px
     * 1dp : 3.0px
     * 1dp : 4.0px
     */
    public static float getScaleDensity() {
        return BaseApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
    }

}
