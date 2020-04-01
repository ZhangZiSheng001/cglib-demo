package cn.zzs.cglib;

import java.lang.reflect.Method;
import java.util.HashSet;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @ClassName: LogInterceptor
 * @Description: 日志记录的拦截器
 * @author: zzs
 * @date: 2019年8月31日 下午4:19:34
 */
public class LogInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 设置需要代理拦截的方法
        HashSet<String> set = new HashSet<String>(6);
        set.add("save");
        set.add("delete");
        set.add("update");
        // 进行日志记录
        if(method != null && set.contains(method.getName())) {
            System.out.println("进行" + method.getName() + "的日志记录");
        }
        // 再执行被代理类的方法，这个执行过程使用了FastClass，相比JDK的动态代理执行效率更高
        Object obj2 = proxy.invokeSuper(obj, args);
        return obj2;
    }
}
