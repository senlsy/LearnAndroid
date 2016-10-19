package com.quickartifact.utils.file;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.quickartifact.BaseApplication;
import com.quickartifact.exception.check.BreakActionException;
import com.quickartifact.exception.check.FileBreakException;
import com.quickartifact.exception.check.UnKnowException;
import com.quickartifact.exception.runtime.InitializationException;
import com.quickartifact.utils.check.CheckUtils;
import com.quickartifact.utils.date.DateUtils;
import com.quickartifact.utils.device.DeviceUtils;
import com.quickartifact.utils.file.callback.FileReadCallback;
import com.quickartifact.utils.log.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * Description: 文件操作的工具类
 * 所有文件的定义，均要求通过FileUtils来定义，方便统一管理
 *
 * @author mark.lin
 * @date 16/3/21 下午4:41
 */
public final class FileUtils {


    private FileUtils() {
    }


    //=======================================================
    // 文件基本操作
    //=======================================================

    /**
     * 定义一个文件，app所有file文件定义尽量采用该方法，方便统一管理
     *
     * @param dir      DirEnum定义的目录，文件目录会被mkdirs
     * @param fileName 文件名
     * @return 返回File，file并未被create
     */
    public static File defindFile(DirEnum dir, @NonNull String fileName) {
        File dirPaht = getDir(dir);
        return new File(dirPaht, fileName);
    }

    /**
     * 获取已存在的文件
     *
     * @param dir      目录
     * @param fileName 文件名
     * @return 不存在返回null
     */
    public static File getFile(DirEnum dir, @NonNull String fileName) {
        File file = defindFile(dir, fileName);
        if (CheckUtils.checkFileExists(file)) {
            return file;
        } else {
            return null;
        }
    }

    /**
     * 创建一个新文件，文件已存在则删除新建
     *
     * @param dir      目录
     * @param fileName 文件
     * @return 创建成功返回当前文件，创建失败返回null
     */
    public static File createNewFile(DirEnum dir, String fileName) {
        return createNewFile(defindFile(dir, fileName));
    }

    /**
     * 创建一个新文件
     *
     * @param file 需要create的file
     * @return 创建成功返回当前文件，创建失败返回null
     */
    public static File createNewFile(@NonNull File file) {
        if (file == null) {
            return null;
        }

        if (file.exists() && !file.delete()) {
            return null;
        }

        try {

            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                return null;
            }
            if (file.createNewFile()) {
                return file;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * nio方式文件复制
     *
     * @param source         源文件
     * @param target         目标文件
     * @param isDeleteSource 是否删除源文件
     * @return 成功返回true，失败返回false
     */
    public static boolean copyFile(File source, @NonNull File target, boolean isDeleteSource) {

        if (!CheckUtils.checkFileExists(source) || target == null) {
            return false;
        }

        target = createNewFile(target);
        if (!CheckUtils.checkFileExists(target)) {
            return false;
        }

        FileChannel inc = null;
        FileChannel ouc = null;
        FileInputStream ins = null;
        FileOutputStream ous = null;
        try {

            ins = new FileInputStream(source);
            ous = new FileOutputStream(target);
            inc = ins.getChannel();
            ouc = ous.getChannel();
            inc.transferTo(0, inc.size(), ouc);

        } catch (IOException e) {
            e.printStackTrace();
            return false;//有return，那么finally执行结束后，finally后面的代码不执行
        } finally {
            close(ins);
            close(inc);
            close(ous);
            close(ouc);
        }
        target.setLastModified(source.lastModified());
        if (isDeleteSource) {
            delete(source);
        }
        return true;
    }

    /**
     * 删除文件
     *
     * @param dir      目录
     * @param fileName 文件名
     * @return 成功true，失败false
     */
    public static boolean delte(DirEnum dir, String fileName) {
        return delete(defindFile(dir, fileName));
    }

    /**
     * 删除文件
     *
     * @param file 需要被删除的文件
     * @return 成功true，失败false
     */
    public static boolean delete(File file) {
        try {
            return deleteFile(file);
        } catch (BreakActionException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件，通过抛出异常来，实现文件是否删除成功
     *
     * @param file 需要被删除的文件
     * @return 成功返回true，失败抛出异常
     * @throws BreakActionException 删除失败抛出
     */
    private static boolean deleteFile(File file) throws BreakActionException {

        if (!CheckUtils.checkFileExists(file)) {
            return true;
        }

        boolean isDeleted;
        if (file.isFile()) {
            isDeleted = file.delete();
        } else {
            String[] childFiles = file.list();
            for (String childName : childFiles) {
                File childFile = new File(file, childName);
                isDeleted = deleteFile(childFile);
                if (!isDeleted) {
                    throw new BreakActionException("delete file fail:" + childFile.getAbsolutePath());
                }
            }
            isDeleted = file.delete();
        }

        if (!isDeleted) {
            throw new BreakActionException("delete file fail:" + file.getAbsolutePath());
        }
        return isDeleted;
    }


    //==========================================
    //获取定义的目录
    //==========================================


    /**
     * 获取DirEnum定义的目录
     *
     * @param dir DirEnum枚举
     * @return 失败返回null
     */
    public static File getDir(@NonNull DirEnum dir) {

        if (dir == null) {
            return null;
        }

        File path = null;
        if (dir.getLevel() == DirEnum.LEVEL_INNER_APP) {
            path = getInnerAppDir(dir);

        } else if (dir.getLevel() == DirEnum.LEVEL_EXTERNAL_APP) {
            path = getExternalAppDir(dir);

        } else if (dir.getLevel() == DirEnum.LEVEL_EXTERNAL_SD) {
            path = getExternalSDCardDir(dir);

        } else if (dir.getLevel() == DirEnum.LEVEL_DEVICE) {
            switch (dir) {
                case DEVICE_INNER_CAHCE:
                    path = getDeviceInnerCacheDir();
                    break;
                case DEVICE_EXTERNAL_CACHE:
                    path = getDeviceExternalCacheDir();
                    break;
            }

        }

        if (path == null) {
            LogUtils.w("create dir fail :" + dir.getPath());
        }
        return path;
    }

    /**
     * 1、获取内置存储-缓存文件夹，注意：缓存文件夹会被不定期清理
     * 2、目录：/data/data/package_name/cache
     *
     * @return 失败返回null
     */
    private static File getDeviceInnerCacheDir() {
        File dir = BaseApplication.getContext().getCacheDir();
        if (CheckUtils.checkFileExists(dir)) {
            return dir;
        } else {
            return null;
        }
    }

    /**
     * 1、获取外置存储-缓存文件夹，注意：缓存文件夹会被不定期清理
     * 2、目录：sdcard/Android/package_name/cache
     *
     * @return 失败返回null
     */
    private static File getDeviceExternalCacheDir() {
        File dir = BaseApplication.getContext().getExternalCacheDir();
        if (CheckUtils.checkFileExists(dir)) {
            return dir;
        } else {
            return null;
        }
    }


    /**
     * 1、获取件内置存储App应用程序目录下的文夹
     * 2、目录：/data/data/package.name/INNER_ROOT_DIR/dirEnum
     *
     * @param dirEnum
     * @return 失败返回null
     */
    private static File getInnerAppDir(@NonNull DirEnum dirEnum) {

        File rootDir = BaseApplication.getContext().getDir(DirEnum.INNER_ROOT_DIR, Context.MODE_PRIVATE);
        if (!CheckUtils.checkFileExists(rootDir)) {
            return null;
        }

        File dir = new File(rootDir.getAbsolutePath(), dirEnum.getPath());
        if (CheckUtils.checkFileExists(dir)) {
            return dir;
        } else {
            return dir.mkdirs() ? dir : null;
        }
    }

    /**
     * 1、获取外置存储App应用程序目录下的文件夹
     * 2、目录：sdcard/Android/data/package_name/files/dirEnum/
     *
     * @param dirEnum
     * @return 失败返回null
     */
    private static File getExternalAppDir(DirEnum dirEnum) {

        if (CheckUtils.checkExternalStoreWriteable()) {
            File dir = BaseApplication.getContext().getExternalFilesDir(dirEnum.getPath());
            if (CheckUtils.checkFileExists(dir)) {
                return dir;
            } else {
                return dir.mkdirs() ? dir : null;
            }
        }
        return null;
    }

    /**
     * 1、获取内置存储SD卡根目录下的文件夹
     * 2、目录：sdcard/dirEnum
     *
     * @param dirEnum
     * @return 失败返回null
     */
    private static File getExternalSDCardDir(DirEnum dirEnum) {

        if (CheckUtils.checkExternalStoreWriteable()) {
            File dir = new File(Environment.getExternalStorageDirectory(), dirEnum.getPath());
            if (CheckUtils.checkFileExists(dir)) {
                return dir;
            } else {
                return dir.mkdirs() ? dir : null;
            }
        }
        return null;


    }


    //==================================
    //输出流操作,提供最原始的字节流操作
    //==================================


    /**
     * 将string写到文件，默认指定字符集utf-8
     *
     * @param str    写出的字符串
     * @param file   写出的文件
     * @param append 是否追加
     * @return 成功返回true，失败返回false
     */
    public static boolean writeStringToFile(String str, File file, boolean append) {
        return writeStringToFile(str, file, FileConfig.ENCODING_CHARSET, append);
    }

    /**
     * 将string根绝指定字符集写到文件
     *
     * @param str     写出的字符串
     * @param file    写出的文件
     * @param charset 写出的字符集编码
     * @param append  是否以追加的方式
     * @return 成功返回true，失败返回false
     */
    public static boolean writeStringToFile(String str, File file, String charset, boolean append) {
        try {
            return writeByteToFile(str.getBytes(charset), file, append);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 将byte[]写到文件
     *
     * @param data   需要写出的byte[]字节数组
     * @param file   被写出的文件
     * @param append 是否以追加的方式
     * @return 成功返回true，失败返回false
     */
    public static boolean writeByteToFile(byte[] data, File file, boolean append) {

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return writeByteToOutputStream(data, fos);
    }

    /**
     * 将btye[]写到输出流。装饰了BufferedOutputStream
     *
     * @param data         需要写出的byte[]字节数组
     * @param outputStream 被写出的输出流
     * @return 成功返回true，失败返回false
     */
    public static boolean writeByteToOutputStream(byte[] data, OutputStream outputStream) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(outputStream, FileConfig.BUFFER_SIZE_WRITE);
            bos.write(data);
            bos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(outputStream);
        }
    }

    //==================================
    //输入流操作,提供最原始的字节流操作
    //==================================


    /**
     * 从file读取utf-8对应的string字符串
     *
     * @param file 被读取的文件
     * @return 读取的字符串，失败返回null
     */
    public static String readStringFromFile(File file) {
        return readStringFromFile(file, FileConfig.ENCODING_CHARSET);
    }

    /**
     * 从file读取指定charset对应的string字符串
     *
     * @param file    被读取的文件
     * @param charset 指定转换成字符串的字符集
     * @return 读取的字符串，失败返回null
     */
    public static String readStringFromFile(File file, String charset) {

        byte[] data = readByteFromFile(file);
        if (data == null) {
            return null;
        }

        try {
            return new String(data, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从文件读取byte[]
     *
     * @param file 被读取的文件
     * @return 读到的byte[]字节数组，失败返回null
     */
    public static byte[] readByteFromFile(File file) {
        FileInputStream ins;
        try {
            ins = new FileInputStream(file);
            return readByteFromInputStream(ins);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BreakActionException e) {
            new FileBreakException("file breakdown :" + file.getAbsolutePath()).printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流中读取字节数组。装饰了BufferedInputStream
     *
     * @param inputStream 读取的输入流
     * @return 读取的byte[]字节数组, 失败返回null。
     * @throws FileBreakException 可读长度！=实际读取长度，抛出损坏异常
     */
    public static byte[] readByteFromInputStream(InputStream inputStream) throws BreakActionException {
        try {

            if (inputStream == null || inputStream.available() == 0) {
                return null;
            }
            //缓冲流装饰提高效率
            BufferedInputStream bis = new BufferedInputStream(inputStream, FileConfig.BUFFER_SIZE_READ);
            byte[] buf = new byte[bis.available()];
            int readLength = bis.read(buf);

            if (buf.length == readLength) {
                return buf;
            } else {
                throw new BreakActionException("inputStream availableLength(" + readLength + ") != readLength(" + bis.available() + ")");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        return null;

    }

    /**
     * 1、从inputStream读取数据转换成所需的结果(byte[]--> T)
     * 2、callback必须进行初始化
     * 3、同步：直接获取转换结果
     * 4、异步：通过callback回调
     *
     * @param inputStream 读取的输入流
     * @param callback    读取过程的回调
     * @return 转换的T结果，失败返回null。（先回调，后return）
     */
    public static <T> T readFromInputStream(InputStream inputStream, FileReadCallback<T> callback) {

        if (callback == null) {
            throw new InitializationException("Parameter: callback(FileReadCallback.class) must initialization");
        }

        try {
            //缓冲流进行缓冲，提高效率
            BufferedInputStream bis = new BufferedInputStream(inputStream, FileConfig.BUFFER_SIZE_READ);
            int total = inputStream.available();
            if (total == 0) {
                T result = callback.parseResult(null); //有可能为null
                callback.finish(result);
                return result;
            }

            //通过虚拟文件对实现对读取数据转换
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[FileConfig.BUFFER_SIZE_READ];//一次读取1024个字节
            int progress = 0;
            int len;
            callback.start(total);

            while ((len = bis.read(buf)) != -1) {
                baos.write(buf, 0, len);
                progress += len;
                callback.progress(total, progress);
            }
            baos.flush();

            byte[] data = baos.toByteArray();
            if (data.length == total) {
                T result = callback.parseResult(data);
                callback.finish(result);
                return result;
            } else {
                throw new FileBreakException("Parameter: inputStream read length(" + data.length + ")!= inputStream available(" + total + ")");
            }

        } catch (IOException e) {
            e.printStackTrace();
            callback.error(e);
        } catch (FileBreakException e) {
            e.printStackTrace();
            callback.error(e);
        } finally {
            close(inputStream);
        }
        return null;
    }

    //=======================
    //关闭操作
    //======================

    /**
     * 关闭输入流
     */
    public static void close(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输出流
     */
    public static void close(OutputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭reader
     */
    public static void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭writer
     */
    public static void close(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭channel
     */
    public static void close(Channel channel) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //========================================================
    // App常用业务方法
    //========================================================

    /**
     * 读取assets中的文件string,默认字符集编码UTF-8
     *
     * @param fileName 文件名
     * @return 读取的字符串, 失败或空返回null
     */
    public static String readStrFromAssetsFile(String fileName) {
        byte[] data = readByteFromAssetsFile(fileName);

        if (data == null) {
            return null;
        }
        try {
            return new String(data, FileConfig.ENCODING_CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取assets中的文件
     *
     * @param fileName assets文件夹下的文件名
     * @return 读取的byte[]字节数组
     */
    public static byte[] readByteFromAssetsFile(String fileName) {
        InputStream ins = null;
        try {
            ins = BaseApplication.getContext().getResources().getAssets().open(fileName);
            if (ins.available() <= 0) {
                return null;
            }
            return readByteFromInputStream(ins);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BreakActionException e) {
            new FileBreakException("assets/file breakdown :" + fileName).printStackTrace();
        } finally {
            close(ins);
        }
        return null;
    }


    /**
     * 写日志到文件
     * 1、每天记录一个文件
     * 2、3天清理一次，保留5个记录
     *
     * @param logContent 需要被记录的日志
     */
    public static void writeLogToFile(String logContent) {

        if (CheckUtils.checkStrHasEmpty(logContent)) {
            return;
        }

        long time = System.currentTimeMillis();
        String fileName = DateUtils.getLogFileName(time) + FileConfig.FILE_TYPE_QIJIA;//日期文件名.qijia
        File dir = getDir(DirEnum.EXTERNAL_SD_LOG);
        if (dir == null) {
            return;
        }

        File logFile = new File(dir, fileName);
        StringBuffer sb = new StringBuffer();
        sb.append(System.getProperty("line.separator"));
        sb.append("==============");
        sb.append(DateUtils.getLogCrashTime(time));
        sb.append("==============");
        sb.append(System.getProperty("line.separator"));
        sb.append(logContent);
        writeStringToFile(sb.toString(), logFile, true);
        //TODO:3天清理一次，保留5个记录

    }

    /**
     * 将异常记录到异常日志文件
     *
     * @param exception 需要被记录的异常
     */
    public static void wirteExceptionToFile(Throwable exception) {

        if (exception == null) {
            return;
        }

        long time = System.currentTimeMillis();
        String fileName = DateUtils.getLogFileName(time) + FileConfig.FILE_TYPE_QIJIA;//日期文件名.qijia
        File dir = getDir(DirEnum.EXTERNAL_SD_CRASH);
        if (dir == null) {
            return;
        }

        File erroFile = new File(dir, fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(erroFile, true);
            PrintStream ps = new PrintStream(os);
            ps.append(System.getProperty("line.separator"));
            ps.append("==============");
            ps.append(DateUtils.getLogCrashTime(time));
            ps.append("==============");
            ps.append(System.getProperty("line.separator"));
            exception.printStackTrace(ps);
            ps.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        close(os);
        //TODO:3天清理一次，保留5个记录
    }


    //======================================
    //获取文件类型
    //======================================


    /**
     * 获取文件扩展名，包含“.”。
     *
     * @param file 文件完成名称
     * @return 返回".后缀名"
     * @throws UnKnowException 无法识别
     */
    public static String getStrSuffix(String file) throws UnKnowException {

        if (CheckUtils.checkStrHasEmpty(file) || !file.contains(".")) {
            throw new UnKnowException(file);
        }

        String suffix = file.substring(file.lastIndexOf("."));
        if (suffix.length() == 1) {
            throw new UnKnowException(file);
        } else {
            return suffix;
        }

    }

    /**
     * 获取图片文件的扩展名，返回值包含“.”。
     *
     * @param str 被解析字符串
     * @return 返回FileConfig定义的图片后缀，无法识别默认返回.jpeg
     */
    public static String getImageSuffix(String str) {

        String suffix = null;

        try {
            suffix = getStrSuffix(str);
        } catch (UnKnowException e) {
            e.printStackTrace();
            return FileConfig.FILE_TYPE_IMAGE_DEFAULT;
        }

        if (FileConfig.FILE_TYPE_JPG.equals(suffix)) {
            return FileConfig.FILE_TYPE_JPG;

        } else if (FileConfig.FILE_TYPE_JPEG.equals(suffix)) {
            return FileConfig.FILE_TYPE_JPEG;

        } else if (FileConfig.FILE_TYPE_PNG.equals(suffix)) {
            return FileConfig.FILE_TYPE_PNG;

        } else if (FileConfig.FILE_TYPE_WEBP.equals(suffix)) {
            return FileConfig.FILE_TYPE_WEBP;

        } else {
            return FileConfig.FILE_TYPE_IMAGE_DEFAULT;
        }
    }

    /**
     * 解析str，获取对应的文件名
     *
     * @param str ：完整文件名、url地址
     * @return 返回对应文件名
     * @throws UnKnowException 无法识别
     */
    public static String getFileName(String str) throws UnKnowException {

        if (CheckUtils.checkStrHasEmpty(str)) {
            throw new UnKnowException(str);
        }

        int a = str.lastIndexOf(File.separator);
        int b = str.lastIndexOf("/");
        String fileName;
        if (a >= b && a >= 0 && a < str.length() - 1) {
            fileName = str.substring(a + 1);
        } else if (b > a && b >= 0 && b < str.length() - 1) {
            fileName = str.substring(b + 1);
        } else {
            //分割符之后没含有字符的情况
            throw new UnKnowException(str);
        }
        return fileName;
    }

    /**
     * 解析str，获取对应的文件名
     *
     * @param str ：完成文件名、url地址
     * @return 返回解对应文件名，失败返回temp_yyyyMMddHHmmss.qijia
     */
    public static String decodeFileName(String str) {
        try {
            return getFileName(str);
        } catch (Exception e) {
            e.printStackTrace();
            return DateUtils.getFileName() + "-" + DeviceUtils.generateUuid() + FileConfig.FILE_TYPE_QIJIA;
        }

    }


}
