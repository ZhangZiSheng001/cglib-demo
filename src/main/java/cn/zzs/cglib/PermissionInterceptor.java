package cn.zzs.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 权限校验的Advice
 * @author: zzs
 * @date: 2019年8月31日 下午4:19:34
 */
public class PermissionInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.err.println("校验用户是否有访问" + method.getName() + "接口的权限");
        Object obj2 = proxy.invokeSuper(obj, args);
        return obj2;
    }

}
