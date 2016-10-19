package frogermcs.io.githubclient.pv_interface;

import com.google.common.collect.ImmutableList;

import frogermcs.io.githubclient.data.model.Repository;
import frogermcs.io.githubclient.persenter.BasePersenter;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/10/12 15:08
 * 修改备注：
 */
public interface RepositoriesListConstruct {

    interface RepositoriesListPersenter extends BasePersenter {

        /**
         * 加载账户列表数据
         */
        void loadRepositories();
    }

    interface RepositoriesListView {

        /**
         * 显示加载中ui
         *
         * @param loading
         */
        void showLoading(boolean loading);

        /**
         * 返回请求数据，更新列表
         *
         * @param repositories
         */
        void setRepositories(ImmutableList<Repository> repositories);
    }
}
