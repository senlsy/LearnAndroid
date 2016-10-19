package com.quickartifact.utils.file;

/**
 * Description: 对文件夹划分了四个区域
 * 0：android FreamWork对app设定的文件夹
 * 1：内置存储app根目录下创建的文件夹
 * 2：外置存储app根目录下创建的文件夹
 * 3：SD卡根目录下创建的文件夹
 *
 * @author mark.lin
 * @date 2016/9/8 16:41
 */
public enum DirEnum {

    /**
     * android FreamWork对app设定的文件夹
     * DEVICE_*：不允许待路径符
     */
    DEVICE_INNER_CAHCE("inner_cache", 0),
    DEVICE_EXTERNAL_CACHE("externale_cache", 0),

    /**
     * 内置存储应用目录：data/data/package_name/APP_ROOT/INNER_*
     * INNER_* ：允许带路径符
     */
    INNER_APP_TEMP("temp", 1),

    /**
     * 外置存储应用目录：...sdcar../Android/data/app's package/files/EXTERNAL_APP_*
     * EXTERNAL_APP_* ：允许带路径符
     */
    EXTERNAL_APP_TEMP("temp", 2),


    /**
     * SD卡根目录下的文件夹：sdcar/EXTERNAL_SD_*
     * EXTERNAL_SD_* ：允许带路径符
     */
    EXTERNAL_SD_LOG("quick/log", 3),//存放app日志
    EXTERNAL_SD_CRASH("quick/crash", 3),//存放app未捕捉到的异常

    EXTERNAL_SD_TRANSCACHE_IMAGE_COMPRESS("quick/trans_cache/compress", 3),//图片压缩缓存文件夹
    EXTERNAL_SD_TRANSCACHE_IMAGE_DOWNLOAD("quick/trans_cache/download", 3),//图片下载缓存文件夹

    EXTERNAL_SD_CACHE_FILE("quick/cache/file", 3),//文件缓存文件夹
    EXTERNAL_SD_CACHE_MODEL("quick/cache/model", 3);//网络请求数据缓存


    /**
     * 应用程序内部存储根目录
     */
    public static final String INNER_ROOT_DIR = "root";

    /**
     * 目录等级定义
     */
    public static final int LEVEL_DEVICE = 0;
    public static final int LEVEL_INNER_APP = 1;
    public static final int LEVEL_EXTERNAL_APP = 2;
    public static final int LEVEL_EXTERNAL_SD = 3;

    private String mPath;
    private int mLevel;


    DirEnum(String path, int level) {
        mPath = path;
        mLevel = level;
    }

    public String getPath() {
        return mPath;
    }

    public int getLevel() {
        return mLevel;
    }
}
