package com.quickartifact.utils.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 类描述：时间处理工具类，返回指定的时间格式字符串
 * 创建人：mark.lin
 * 创建时间：2016/9/28 15:26
 * 修改备注：
 */
public final class DateUtils {


    private DateUtils() {

    }

    /**
     * 返回当前时间的yyyyMMddHHmmss时间格式作为文件名
     */
    public static String getFileName() {
        return format(DateFormatEnum.FORMAT_6.getFormat(), new Date());
    }


    /**
     * 返回time时间的yyyy-MM-dd时间格式作为日志文件的文件名
     */
    public static String getLogFileName(long time) {
        return format(DateFormatEnum.FORMAT_1.getFormat(), new Date(time));
    }

    /**
     * 返回time时间的HH:mm:ss时间格式作为日志记录的时间点
     */
    public static String getLogCrashTime(long time) {
        return format(DateFormatEnum.FORMAT_5.getFormat(), new Date(time));
    }


    /**
     * 格式化date返回指定格式的时间字符串
     *
     * @param type 格式化样式
     * @param date 格式化的时间
     * @return 格式化后的时间字符串
     */
    private static String format(String type, Date date) {
        SimpleDateFormat format = new SimpleDateFormat(type, Locale.US);
        return format.format(date);
    }
}
