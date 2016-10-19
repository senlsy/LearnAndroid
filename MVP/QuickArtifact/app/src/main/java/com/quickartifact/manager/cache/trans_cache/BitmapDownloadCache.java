package com.quickartifact.manager.cache.trans_cache;

import android.graphics.Bitmap;

import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.file.DirEnum;
import com.quickartifact.utils.file.FileConfig;
import com.quickartifact.utils.image.BitmapUtils;
import com.quickartifact.utils.signature.SignatureUtils;
import com.quickartifact.utils.signature.SignatureEnum;

import java.io.File;

/**
 * Description: 将Bitmap图片缓存在指定目录,并保存成文件。
 * 缓存文件名：key（md5）.qijia<b/>
 *
 * @author mark.lin
 * @date 2016/9/20 17:52
 */
public class BitmapDownloadCache extends TransCache<Bitmap, File> {


    private static class HolderClass {
        private final static BitmapDownloadCache instance = new BitmapDownloadCache();
    }

    public static BitmapDownloadCache getInstance() {
        return HolderClass.instance;
    }

    private BitmapDownloadCache() {
        mCacheDir = DirEnum.EXTERNAL_SD_TRANSCACHE_IMAGE_DOWNLOAD;
    }


    @Override
    protected void init() {

    }


    /**
     * bitmap转换成文件，存储在该类对应的目录下
     *
     * @param key    任意字符，如果要指定缓存的文件类型，可以在key后待上扩展名。例如 key.png|key.jpeg
     * @param bitmap 需要保存的文件
     * @return true：成功，false：失败
     */
    @Override
    public boolean put(String key, Bitmap bitmap) {
        if (CheckUtils.checkStrHasEmpty(key)) {
            return false;
        }
        return BitmapUtils.compressBitmapToFile(bitmap, FileUtils.defindFile(mCacheDir, transformKey(key)));
    }

    /**
     * 获取key对应的缓存文件，不存在返回null
     *
     * @param key put时候的key
     * @return 缓存文件、null
     */
    @Override
    public File get(String key) {
        if (CheckUtils.checkStrHasEmpty(key)) {
            return null;
        }
        return FileUtils.getFile(mCacheDir, transformKey(key));
    }

    @Override
    public boolean isExpiry(String key) {
        return false;
    }

    @Override
    public boolean remove(String key) {
        return FileUtils.delete(get(key));
    }


    @Override
    public boolean contains(String key) {
        return CheckUtils.checkFileExists(get(key));
    }

    @Override
    public boolean contains(File file) {
        return CheckUtils.checkFileExists(file);
    }

    /**
     * 获取返回文件名：key（md5）+ key（imageType）
     */
    private String transformKey(String key) {
        return SignatureUtils.encoding(key, SignatureEnum.MD5) + FileConfig.FILE_TYPE_QIJIA;
    }

}
