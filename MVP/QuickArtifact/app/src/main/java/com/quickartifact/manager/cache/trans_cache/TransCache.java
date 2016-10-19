package com.quickartifact.manager.cache.trans_cache;

import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.file.DirEnum;

import java.io.File;

/**
 * Description: 缓存转换类<br/>
 * 1、T表示：放入的实体。<br/>
 * 2、S表示：取出的实体<br/>
 * 3、实现的子类根据自身完成T-->S的转换<br/>
 * 4、缓存S<br/>
 *
 * @author mark.lin
 * @date 2016/9/20 17:36
 */
public abstract class TransCache<T, S> {

    protected DirEnum mCacheDir;

    /**
     * 缓存在哪里，提示作用而已
     */
    public String getCachePath() {
        File dir = FileUtils.getDir(mCacheDir);
        if (CheckUtils.checkFileExists(dir)) {
            return dir.getAbsolutePath();
        } else {
            return getClass().getSimpleName() + " create cache dir :" + mCacheDir.getPath() + " fail!";
        }
    }

    /**
     * 初始化
     */
    protected void init() {
    }

    /**
     * 放入缓存的实体
     */
    public abstract boolean put(String key, T t);

    /**
     * 取出缓存实体
     */
    public abstract S get(String key);

    /**
     * 缓存实体已过期
     *
     * @return true表示已过期
     */
    public abstract boolean isExpiry(String key);

    /**
     * 移除key对应的缓存实体
     */
    public abstract boolean remove(String key);

    /**
     * 清除所有缓存
     */
    public boolean clear() {
        return FileUtils.delete(FileUtils.getDir(mCacheDir));
    }

    /**
     * 是否还有某个键
     */
    public abstract boolean contains(String key);


    /**
     * 是否含有某个实体
     */
    public abstract boolean contains(S t);


}
