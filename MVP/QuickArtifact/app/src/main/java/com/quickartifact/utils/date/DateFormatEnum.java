package com.quickartifact.utils.date;

/**
 * Description: 常用的日期格式
 *
 * @author mark.lin
 */
public enum DateFormatEnum {
    FORMAT_1("yyyy-MM-dd"),
    FORMAT_2("yyyy-MM-dd-HH-mm-ss"),
    FORMAT_3("yyyy年MM月dd日HH时mm分"),
    FORMAT_4("yyyy-MM-dd HH:mm:ss"),
    FORMAT_6("yyyyMMddHHmmss"),
    FORMAT_5("HH:mm:ss");

    private String mFormat;

    DateFormatEnum(String format) {
        mFormat = format;
    }


    /**
     * 返回格式字符串
     *
     * @return
     */
    public String getFormat() {
        return mFormat;
    }
}
