package com.yuexunit.android.library.library_utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.text.TextUtils;

import com.yuexunit.android.library.library_utils.log.Logger;


public class FileUtil
{
    
    /**
     * @Title: getRootDir
     * @Description: 获取应用程序根目录，卸载应用程序时，会一并删除该目录
     * @param context
     * @return 返回引用程序根目录
     * @throws Exception
     */
    public static File getRootDir(Context context) {
        File rootDir=context.getExternalFilesDir(null);
        if(rootDir == null)
            rootDir=context.getFilesDir();
        if(rootDir == null)
            Logger.w("FileUtil.getRootDir 获取根目录为 null", false);
        return rootDir;
    }
    
    /**
     * @Title: createFile
     * @Description:根据dir+fileName创建一个文件，不覆盖已存在文件
     * @param dir
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createFile(String dir, String fileName) throws IOException {
        return createFile(new File(dir), fileName, false);
    }
    
    /**
     * @Title: createFile
     * @Description:根据dir+fileName创建一个文件，不覆盖已存在文件
     * @param dir
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createFile(File dir, String fileName) throws IOException {
        return createFile(dir, fileName, false);
    }
    
    /**
     * @Title: createFile
     * @Description:根据dir+fileName创建一个文件
     * @param dir
     * @param fileName
     * @param converOld
     *            是否覆盖已存在的文件
     * @return
     * @throws IOException
     */
    public static File createFile(String dir, String fileName, boolean converOld) throws IOException
    {
        return createFile(new File(dir), fileName, converOld);
    }
    
    /**
     * @Title: createFile
     * @Description:根据dir+fileName创建一个文件
     * @param dir
     * @param fileName
     * @param converOld
     *            是否覆盖已存在的文件
     * @return
     * @throws IOException
     */
    public static File createFile(File dir, String fileName, boolean coverOld) throws IOException
    {
        if(dir == null || TextUtils.isEmpty(fileName)){
            return null;
        }
        if( !dir.isDirectory()){
            Logger.w("FileUtil.getRootDir() dir参数不是文件夹类型", false);
            return null;
        }
        if( !dir.exists())
            dir.mkdirs();
        File createFile=new File(dir, fileName);
        if( !createFile.exists())
        {
            createFile.createNewFile();
        }
        else
        {
            if(coverOld){
                createFile.delete();
                createFile.createNewFile();
            }
        }
        return createFile;
    }
}
