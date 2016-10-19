package com.yuexunit.android.library.library_http.util;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: CharsetUtils
 * @Description: 字符集转换工具，感谢wyouflf作者提供的开源框架
 * @author LinSQ
 * @date 2015-5-8 下午4:55:37
 * @version
 * @note
 */
/**
 * @ClassName: CharsetUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author LinSQ
 * @date 2015-5-8 下午5:02:16
 * @version
 * @note
 */
public class CharsetUtils
{
    
    public CharsetUtils(){
    }
    
    /**
     * @Description: 编码转换将参数str转换成所需字符集charset对应的字符串
     * @param
     * @return String
     * @throws UnsupportedEncodingException
     * @note
     */
    public static String toCharset(final String str, final String charset, int judgeCharsetLength) throws UnsupportedEncodingException {
        String oldCharset=getEncoding(str, judgeCharsetLength);
        return new String(str.getBytes(oldCharset), charset);
    }
    
    /**
     * @Description: 获取字符串的编码字符集
     * @param
     * @return String
     * @throws UnsupportedEncodingException
     * @note
     */
    public static String getEncoding(final String str, int judgeCharsetLength) throws UnsupportedEncodingException {
        String encode=CharsetUtils.DEFAULT_ENCODING_CHARSET;
        for(String charset:SUPPORT_CHARSET){
            if(isCharset(str, charset, judgeCharsetLength)){
                encode=charset;
                break;
            }
        }
        return encode;
    }
    
    /**
     * @Description: 参数str是否符合charset字符集编码
     * @param
     * @return boolean
     * @throws UnsupportedEncodingException
     * @note
     */
    public static boolean isCharset(final String str, final String charset, int judgeCharsetLength) throws UnsupportedEncodingException {
        // 复制一份源字符串，用charset换成字节数组，在用charset转换成串，看是否以源字符串一致。一致说名就是该字符集
        String temp=str.length() > judgeCharsetLength ? str.substring(0, judgeCharsetLength) : str;
        return temp.equals(new String(temp.getBytes(charset), charset));
    }
    
    public static final String DEFAULT_ENCODING_CHARSET=HTTP.DEFAULT_CONTENT_CHARSET;
    public static final List<String> SUPPORT_CHARSET=new ArrayList<String>();
    static{
        SUPPORT_CHARSET.add("ISO-8859-1");
        SUPPORT_CHARSET.add("GB2312");
        SUPPORT_CHARSET.add("GBK");
        SUPPORT_CHARSET.add("GB18030");
        SUPPORT_CHARSET.add("US-ASCII");
        SUPPORT_CHARSET.add("ASCII");
        SUPPORT_CHARSET.add("ISO-2022-KR");
        SUPPORT_CHARSET.add("ISO-8859-2");
        SUPPORT_CHARSET.add("ISO-2022-JP");
        SUPPORT_CHARSET.add("ISO-2022-JP-2");
        SUPPORT_CHARSET.add("UTF-8");
    }
}
