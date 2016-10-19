package com.quickartifact.manager.session;

/**
 * Description: 管理会话类
 *
 * @author liuranchao
 * @date 16/3/31 下午4:38
 */
public final class SessionManager {

    private static SessionManager sInstance;

    /**
     * 当前的页面名称
     */
    private String mCurrentPageName;

    /**
     * 前一个页面的名称
     */
    private String mPrePageName;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        synchronized (SessionManager.class) {
            if (sInstance == null) {
                sInstance = new SessionManager();
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     */
    public void init() {

    }


    /**
     * 会话开始
     */
    public void start() {

    }

    /**
     * 本次会话结束处理
     */
    public void end() {

    }

    /**
     * Login之后处理
     */
    public void login() {

    }

    /**
     * Logout之后处理
     */
    public void logout() {
    }


    public String getPrePageName() {
        return mPrePageName;
    }

    public void setPrePageName(String prePageName) {
        mPrePageName = prePageName;
    }

    public String getCurrentPageName() {
        return mCurrentPageName;
    }

    public void setCurrentPageName(String currentPageName) {
        mCurrentPageName = currentPageName;
    }

}
