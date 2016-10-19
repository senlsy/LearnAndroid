package com.quickartifact.utils.file.callback;

/**
 * 类描述：文件读取进度回调，并对读取的字节进行结果转换
 * 创建人：mark.lin
 * 创建时间：2016/9/27 10:57
 * 修改备注：
 */
public interface FileReadCallback<T> {


    /**
     * 回调开始
     *
     * @param total 进度总量
     */
    void start(int total);

    /**
     * 进行中的回调
     *
     * @param total    进度总量
     * @param progress 当前进度值
     */
    void progress(int total, int progress);


    /**
     * 对读取的数据btye[]进行转换，转换的结果会传递给finish(T result)
     *
     * @param data 读取到的btye[],有可能为null
     * @return 转换的结果
     */
    T parseResult(byte[] data);


    /**
     * 读取结束，与error()互斥
     *
     * @param result 是parseResult()方法的返回值，有可能为null
     */
    void finish(T result);


    /**
     * 过程中出现异常，与finish()互斥
     *
     * @param exception 捕捉到的异常
     */
    void error(Exception exception);
}
