package com.quickartifact.constant;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/10/11 14:01
 * 修改备注：
 */
public interface BundleKey {

    /**
     * fragment 将临时数据bundle保存进 agreements对应的key
     */
    String INTENT_EXTRA_FRAGMENT_SAVE_BUNDLE_KEY = "intent.extra.fragment.save.bundle.key";

    /**
     * fragment请求权限或启动子activity的requestCode,需要临时保存在bundle
     */
    String INTENT_EXTRA_FRAGMENT_PERMISSIONS_REQUEST_CODE = "intent.extra.fragment.permissions.request.code";
}
