package frogermcs.io.githubclient.utils;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 类描述：
 * 创建人：mark.lin
 * 创建时间：2016/10/12 16:31
 * 修改备注：
 */
public class Test {


    /**
     * 1、构造函数打上@Inject，就会默认成为所有对应Component的组件（条件：拥有相同Scope或没有Scope的Component）
     * 2、打上@Scope 那么在对应的Component实例中就会成为单例组件（条件：相同的@Scope的Component）
     * 3、如果构造函数需要其他参数，所需的参数回到其所归属的Component实例中找
     * 4、Module中provideXXX（）方法的优先级高于这样的提供方式
     * 5、切记：类知识描述，实例对象才能使用。一个Component实例就对应着一批组件，组件的声明周期=Component实例的声明周期
     * 6、打上@Scope时：单例组件在一个Component实例中是单例，并不是在所有Component实例是单例，两个Component实例就有两个单例组件
     */
    @Singleton
    @Inject
    public Test() {
    }

    public void start() {
        Log.e("lintest", String.format("hello word! I am Test=%s", this.hashCode()));
    }
}
