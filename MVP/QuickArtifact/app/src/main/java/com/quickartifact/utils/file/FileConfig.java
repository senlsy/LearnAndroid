package com.quickartifact.utils.file;

/**
 * Description: File模块的常量表
 *
 * @author mark.lin
 * @date 2016/9/20 18:11
 */
public interface FileConfig {


    /**
     * 输入流一次read的长度
     */
    int BUFFER_SIZE_READ = 1024;

    /**
     * 字符<-->字节的转换编码集
     */
    String ENCODING_CHARSET = "UTF-8";

    /**
     * 输出流一次写出的字节长度
     */
    int BUFFER_SIZE_WRITE = 1024;

    /**
     * 无法自动识别的文件名，就以“temp_”+当前时间为文件名
     */

    //==================================
    //常用的文件类型
    //==================================

    String FILE_TYPE_IMAGE_DEFAULT = ".jpg";
    String FILE_TYPE_JPG = ".jpg";
    String FILE_TYPE_JPEG = ".jpeg";
    String FILE_TYPE_PNG = ".png";
    String FILE_TYPE_WEBP = ".webp";

    String FILE_TYPE_QIJIA = ".qijia";
}
