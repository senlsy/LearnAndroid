package com.quickartifact.utils.signature;

/**
 * Description: 加密工具基类
 *
 * @author mark.lin
 * @date 2016/9/5 16:20
 */
public abstract class SignatureUtils {


    abstract String signature(String originalStr);

    abstract String signature(String originalStr, String psw);

    /**
     * 创建对应的加密实例
     *
     * @param type
     * @return
     */
    private static SignatureUtils produce(SignatureEnum type) {
        switch (type) {
            case MD5:
                return new MD5Signature();
            default:
                return new MD5Signature();
        }
    }


    /**
     * 对字符串进行签名
     *
     * @param originalStr 字符串
     * @param type        签名方式
     * @return
     */
    public static String encoding(String originalStr, SignatureEnum type) {
        return produce(type).signature(originalStr);
    }

    public static String encoding(String originalStr, String psw, SignatureEnum type) {
        return produce(type).signature(originalStr, psw);
    }

}
