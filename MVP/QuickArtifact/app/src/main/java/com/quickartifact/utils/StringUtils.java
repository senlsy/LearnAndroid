package com.quickartifact.utils;

import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.quickartifact.BaseApplication;
import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.device.ResourceUtils;

import java.util.Locale;

/**
 * Description: 操作字符工具类
 *
 * @author liuranchao
 * @date 16/3/17 上午9:59
 */
public final class StringUtils {


    private StringUtils() {
    }

    public static String format(String format, Object... obj) {
        return String.format(Locale.getDefault(), format, obj);
    }

    /**
     * 设置TextView显示html
     *
     * @param htmlText HTML格式的文本
     */
    @Nullable
    public static Spanned getHtmlText(String htmlText) {
        if (CheckUtils.checkStrHasEmpty(htmlText)) {
            return null;
        }
        return Html.fromHtml(htmlText);
    }

    /**
     * @param resId      strings.xml
     * @param formatArgs %1$s 替换字符串
     * @author Kevin Liu
     */
    public static String getString(@StringRes int resId, Object... formatArgs) {
        return BaseApplication.getContext().getResources().getString(resId, formatArgs);
    }


    /**
     * 获取html文本变色样式
     * #999999
     *
     * @param content 文本内容
     */
    public static String getHtmlStrWithColor(String colorStr, String content) {
        return "<font color='" + colorStr + "'>" + content + "</font>";
    }


    /**
     * 获取TextView不同颜色的文本
     *
     * @param sourceStr     整个文本
     * @param keyword       要匹配的string
     * @param behindColorId 背景色
     * @param frontColorId  前景色
     */
    @Nullable
    public static SpannableStringBuilder getMatchKeywordColorStr(String sourceStr, String keyword, @ColorRes int behindColorId, @ColorRes int frontColorId) {
        if (CheckUtils.checkStrHasEmpty(sourceStr, keyword)) {
            return null;
        }

        // 背景
        int behindStart = sourceStr.indexOf(keyword);
        int behindEnd = behindStart + keyword.length();
        // 前景
        int frontStart = sourceStr.indexOf(keyword);
        int frontEnd = frontStart + keyword.length();
        SpannableStringBuilder style = new SpannableStringBuilder(sourceStr);

        if (behindStart >= 0 && behindColorId > 0) {
            style.setSpan(new BackgroundColorSpan(ResourceUtils.getColor(behindColorId)), behindStart, behindEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (frontStart >= 0 && frontColorId > 0) {
            style.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(frontColorId)), frontStart, frontEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return style;
    }


    /**
     * 转换成小写
     */
    public static char toLower(char ch) {
        if (ch <= 'Z' && ch >= 'A') {
            return (char) (ch - 'A' + 'a');
        }
        return ch;
    }

    /**
     * 转换成大写
     */
    public static char toUpper(char ch) {
        if (ch <= 'z' && ch >= 'a') {
            return (char) (ch - 32);
        }
        return ch;
    }

    public static String logMeasureSpec(int spece) {
        StringBuilder sb = new StringBuilder();
        final int model = View.MeasureSpec.getMode(spece);
        final int size = View.MeasureSpec.getSize(spece);
        switch (model) {
            case View.MeasureSpec.EXACTLY:
                sb.append("EXACTLY:");
                break;
            case View.MeasureSpec.AT_MOST:
                sb.append("AT_MOST:");
                break;
            case View.MeasureSpec.UNSPECIFIED:
                sb.append("UNSPECIFIED:");
                break;

            default:
                sb.append("unkonw:");
                break;
        }
        sb.append(size);
        return sb.toString();

    }

    /**
     * 去除对应的参数，然后返回去除后的URL
     *
     * @param url
     * @param params
     * @return
     */
    public static String removeParams(String url, String[] params) {
        String reg = null;
        String mUrl = url;
        for (int i = 0; i < params.length; i++) {
            reg = "(?<=[\\?&])" + params[i] + "=[^&]*&?";
            mUrl = mUrl.replaceAll(reg, "");
        }
        mUrl = mUrl.replaceAll("&+$", "");
        return mUrl;
    }

    public static boolean isURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("http://");
    }


}
