package com.quickartifact.utils.check;

/**
 * Description: 正则表达式匹配工具
 *
 * @author mark.lin
 * @date 2016/9/18 14:08
 */
public final class MatcherUtils {

    private MatcherUtils() {

    }


    /**
     * 检查输入的字符串是否匹配正则表达式
     *
     * @param source  检查的字符串
     * @param pattern 正则表达式
     * @return true 匹配，false 不匹配
     */
    public static boolean isMatchPattern(String source, String pattern) {
        if (CheckUtils.checkStrHasEmpty(source)) {
            return false;
        }

        return source.matches(pattern);
    }

}
