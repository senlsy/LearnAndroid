package com.quickartifact.utils.check;

/**
 * Description: TODO
 *
 * @author mark.lin
 * @date 2016/9/20 19:32
 */
public interface CheckConfig {

    /**
     * 验证手机格式
     * <p/>
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     */
    String PATTERN_PHONE = "[1][358]\\d{9}";

    /**
     * 检查是否匹配昵称,昵称必须为1-12位中英文、数字、"-"、 "_'
     */
    String PATTERN_NIK_NAME = "^[\\u4e00-\\u9fa50-9a-zA-Z\\-_]{1,12}$";

}
