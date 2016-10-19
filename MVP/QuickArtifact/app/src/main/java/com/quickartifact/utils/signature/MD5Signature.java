package com.quickartifact.utils.signature;

import com.quickartifact.exception.runtime.SignatureException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description: MD5工具类,这里采用软引用单例，对于占用内如不大且不常用，不必要使用这种方式，这里仅仅是练手
 *
 * @author mark.lin
 * @date 16/4/19 下午6:35
 */
final class MD5Signature extends SignatureUtils {


    MD5Signature() {
    }


    @Override
    String signature(String originalStr) {

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(originalStr.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException("signature type MD5,sinature str:" + originalStr);
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("signature type MD5,sinature str:" + originalStr);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();

    }


    @Override
    String signature(String originalStr, String psw) {
        throw new SignatureException("signature type MD5 is not support signature white password,sinature str:" + originalStr);
    }

    /*static MD5Signature getInstance() {
        MD5Signature obj = HolderClass.instance.get();
        if (obj != null) {
            return obj;
        } else {
            obj = new MD5Signature();
            HolderClass.instance = new SoftReference<>(obj);
            return obj;
        }
    }*/

    /*private static class HolderClass {
        private static SoftReference<MD5Signature> instance = new SoftReference<>(new MD5Signature());
    }*/
}
