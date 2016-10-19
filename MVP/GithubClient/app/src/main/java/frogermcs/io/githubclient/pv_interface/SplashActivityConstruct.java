package frogermcs.io.githubclient.pv_interface;

import frogermcs.io.githubclient.data.model.User;
import frogermcs.io.githubclient.persenter.BasePersenter;

/**
 * 类描述：Splash页面Persenter和SplashView通讯接口
 * 创建人：mark.lin
 * 创建时间：2016/10/12 14:45
 * 修改备注：
 */
public interface SplashActivityConstruct {

    interface SplashPersenter extends BasePersenter {

        /**
         * 登录按钮点击事件
         */
        void onShowRepositoriesClick();


        /**
         * 设置登录的账户名
         */
        void setUsername(String username);

    }

    interface SplashView {

        /**
         * 返回改账户对应的数据，显示RepositoriesList页面
         *
         * @param user 返回的数据
         */
        void showRepositoriesListForUser(User user);

        /**
         * 验证不通过
         */
        void showValidationError();

        /**
         * 显示加载中的UI
         *
         * @param loading
         */
        void showLoading(boolean loading);
    }
}
