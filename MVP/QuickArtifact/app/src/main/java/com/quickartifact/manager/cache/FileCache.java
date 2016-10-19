package com.quickartifact.manager.cache;

import com.quickartifact.exception.runtime.InitializationException;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.file.DirEnum;
import com.quickartifact.utils.file.FileConfig;
import com.quickartifact.utils.signature.SignatureUtils;
import com.quickartifact.utils.signature.SignatureEnum;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;


/**
 * 类描述：文件夹缓存类，单例模式，确保每个缓存文件夹都是单例模式。<br/>
 * 1、完成目标文件到缓存文件夹的拷贝<br/>
 * 2、缓存文件名：key(md5).qijia<br/>
 * <p/>
 * 创建人：mark.lin
 * 创建时间：2016/10/9 10:14
 * 修改备注：
 */
public class FileCache extends Cache<File> {


    private static HashMap<DirEnum, SoftReference<FileCache>> mCacheMap = new HashMap<>();

    public static FileCache getInstance(DirEnum cacheDir) {

        if (cacheDir == null) {
            throw new InitializationException("cacheDir must not be null");
        }

        FileCache cache = getCache(cacheDir);
        if (cache != null) {
            return cache;
        }

        synchronized (FileCache.class) {
            FileCache temp = getCache(cacheDir);
            if (temp == null) {
                temp = new FileCache(cacheDir);
                SoftReference<FileCache> sof = new SoftReference<FileCache>(temp);
                mCacheMap.put(cacheDir, sof);
            }
            return temp;
        }

    }

    private static FileCache getCache(DirEnum cacheDir) {
        if (mCacheMap.containsKey(cacheDir)) {
            SoftReference<FileCache> sof = mCacheMap.get(cacheDir);
            FileCache cache = sof.get();
            if (cache != null) {
                return cache;
            }
        }
        return null;
    }

    private FileCache(DirEnum cacheDir) {
        mCacheDir = cacheDir;
    }

    @Override
    public boolean put(String key, File file) {
        return FileUtils.copyFile(file, FileUtils.defindFile(mCacheDir, transformKey(key)), false);
    }

    @Override
    public File get(String key) {
        return FileUtils.getFile(mCacheDir, transformKey(key));
    }

    @Override
    public boolean isExpiry(String key) {
        return false;
    }

    @Override
    public boolean remove(String key) {
        return FileUtils.delete(FileUtils.defindFile(mCacheDir, transformKey(key)));
    }

    @Override
    public boolean contains(String key) {
        if (get(key) == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean contains(File file) {
        if (get(file.getAbsolutePath()) == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取返回文件名：key（md5）
     */
    private String transformKey(String key) {
        return SignatureUtils.encoding(key, SignatureEnum.MD5) + FileConfig.FILE_TYPE_QIJIA;
    }

}
