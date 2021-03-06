package cn.zzs.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 日志记录的Advice
 * @author: zzs
 * @date: 2019年8月31日 下午4:19:34
 */
public class LogInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.err.println("打印" + method.getName() + "方法的入参");
        // 注意，这里要调用proxy.invokeSuper，而不是method.invoke，不然会出现栈溢出等问题
        Object obj2 = proxy.invokeSuper(obj, args);
        System.err.println("打印" + method.getName() + "方法的出参");
        return obj2;
    }

}
