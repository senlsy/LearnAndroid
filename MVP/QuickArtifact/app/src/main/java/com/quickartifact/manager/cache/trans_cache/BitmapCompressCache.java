package com.quickartifact.manager.cache.trans_cache;

import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.file.FileUtils;
import com.quickartifact.utils.file.DirEnum;
import com.quickartifact.utils.image.BitmapUtils;
import com.quickartifact.utils.signature.SignatureUtils;
import com.quickartifact.utils.signature.SignatureEnum;

import java.io.File;

/**
 * Description: 图片压缩缓存，单例模式（操作不安全）。<p/>
 * 1、对image文件进行压缩缓存。<b/>
 * 2、缓存文件名：key（md5）+ key（imageType）（无法识别key的imageType，默认为jpg）<b/>
 *
 * @author mark.lin
 * @date 2016/9/22 17:36
 */
public class BitmapCompressCache extends TransCache<File, File> {

    public static final int IMAGE_LIMIT_WIDTH = 800;//限制的宽度（单位px）
    private static final int IMAGE_LIMIT_HEIGHT = 1500;//限制的高度（单位px）
    private static final int IMAGE_LIMIT_SIZE = 300;//限制的大小（单位KB）

    private static class HolderClass {
        private final static BitmapCompressCache instance = new BitmapCompressCache();
    }

    public static BitmapCompressCache getInstance() {
        return HolderClass.instance;
    }

    private BitmapCompressCache() {
        mCacheDir = DirEnum.EXTERNAL_SD_TRANSCACHE_IMAGE_COMPRESS;
    }


    /**
     * 对imageFile进行压缩，并缓存到指定目录。
     *
     * @param key   要求用image的完整名称作为key
     * @param image 需要压缩缓存的文件
     * @return true:成功，false：失败
     */
    @Override
    public boolean put(String key, File image) {

        if (CheckUtils.checkStrHasEmpty(key) || !CheckUtils.checkFileExists(image) || !key.equals(image.getAbsolutePath())) {
            return false;
        }

        return BitmapUtils.compressImageFile(
                image,
                FileUtils.defindFile(mCacheDir, transformKey(key)),
                getWidth(image),
                getHeight(image),
                IMAGE_LIMIT_SIZE);
    }

    /**
     * 取出key对应的缓存。<br/>
     * 1、对于获取不到压缩的图片，会对key文件压缩，再返回压缩后的缓存文件。<br/>
     * 2、key文件压缩失败，则返回key文件。<br/>
     * 3、key不是完整文件名，返回null。<br/>
     *
     * @param key 要求以原imageFile的完整文件名作为key。
     * @return 缓存的文件、源文件、null
     */
    @Override
    public File get(String key) {

        if (CheckUtils.checkStrHasEmpty(key)) {
            return null;
        }

        File compressFile = FileUtils.getFile(mCacheDir, transformKey(key));
        if (CheckUtils.checkFileExists(compressFile)) {
            return compressFile;
        }

        File imageFile = new File(key);
        if (CheckUtils.checkFileExists(imageFile)) {
            if (put(key, imageFile)) {
                return get(key);
            } else {
                return imageFile;
            }
        }
        return null;
    }

    @Override
    public boolean isExpiry(String key) {
        return false;
    }

    @Override
    public boolean remove(String key) {
        return FileUtils.delete(FileUtils.getFile(mCacheDir, transformKey(key)));
    }

    @Override
    public boolean contains(String key) {
        if (CheckUtils.checkStrHasEmpty(key)) {
            return false;
        }
        return CheckUtils.checkFileExists(FileUtils.getFile(mCacheDir, transformKey(key)));
    }

    @Override
    public boolean contains(File file) {
        return CheckUtils.checkFileExists(file);
    }

    /**
     * 获取缓存的文件名：key（md5）+ key（imageType）
     */
    private String transformKey(String key) {
        return new StringBuffer()
                .append(SignatureUtils.encoding(key, SignatureEnum.MD5))
                .append(FileUtils.getImageSuffix(key))
                .toString();
    }

    /**
     * 根据图片方向来取得对应的限定宽
     */
    private int getWidth(File imageFile) {
        if (BitmapUtils.getPhotoOrientation(imageFile) == 0 || BitmapUtils.getPhotoOrientation(imageFile) == 180) {
            return IMAGE_LIMIT_WIDTH;
        }
        return IMAGE_LIMIT_HEIGHT;
    }

    /**
     * 根据图片方向来取得对应的限定高
     */
    private int getHeight(File imageFile) {
        if (BitmapUtils.getPhotoOrientation(imageFile) == 90 || BitmapUtils.getPhotoOrientation(imageFile) == 270) {
            return IMAGE_LIMIT_WIDTH;
        }
        return IMAGE_LIMIT_HEIGHT;
    }
}
