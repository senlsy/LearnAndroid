package frogermcs.io.githubclient.persenter;

import frogermcs.io.githubclient.ui.activity.MVP_View;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/10/12 15:13
 * 修改备注：
 */
public interface BasePersenter {

    void attachView(MVP_View view);

    void detachView(MVP_View view);
}
