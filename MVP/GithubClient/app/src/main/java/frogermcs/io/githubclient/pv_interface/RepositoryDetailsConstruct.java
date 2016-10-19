package frogermcs.io.githubclient.pv_interface;

import frogermcs.io.githubclient.persenter.BasePersenter;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/10/12 15:08
 * 修改备注：
 */
public interface RepositoryDetailsConstruct {

    interface RepositoryDetailsPersenter extends BasePersenter {
        void init();
    }

    interface RepositoryDetailsListView {
        void setupUserName(String userName);
    }
}
