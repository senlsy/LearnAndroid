package com.yuexunit.android.library.task;

/**
 * Author: wyouflf Date: 14-5-16 Time: 上午11:25
 */
/**
 * @ClassName: PriorityObject
 * @Description: 优先级队列中的优先级对象类。
 * @author LinSQ
 * @date 2015-4-20 下午10:27:06
 * @version
 * @note
 * @param <E>
 */
public class PriorityObject<E>
{
    
    // 优先级，值
    public final Priority priority;
    public final E obj;
    
    public PriorityObject(Priority priority, E obj){
        this.priority=priority == null ? Priority.DEFAULT : priority;
        this.obj=obj;
    }
}
