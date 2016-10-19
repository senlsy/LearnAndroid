package com.quickartifact.utils.log;

/**
 * Description: 日志打印等级，作用于MarkLog
 *
 * @author mark.lin
 * @date 2016/9/18 14:00
 */
public enum LogLevelEnum {

    VERBOSE(0), DEBUG(1), INFO(2), WARM(3), ERROR(4);

    private int mLevel;

    LogLevelEnum(int level) {
        mLevel = level;
    }

    public int getLevel() {
        return mLevel;
    }
}
