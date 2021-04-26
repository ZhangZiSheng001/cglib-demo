package cn.zzs.cglib;

import org.junit.Test;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;

/**
 * 测试cglib动态代理
 * @author: zzs
 * @date: 2019年8月31日 下午4:50:06
 */
public class CglibTest {

    @Test
    public void test01() throws InterruptedException {
        // 设置输出代理类到指定路径，便于后面分析
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:/growUp/tmp");
        // 创建Enhancer对象
        Enhancer enhancer = new Enhancer();
        // 设置哪个类需要代理
        enhancer.setSuperclass(UserController.class);
        // 设置怎么代理，这里传入的是Callback对象-MethodInterceptor父类
        enhancer.setCallback(new LogInterceptor());
        // 获取代理类实例
        UserController userController = (UserController)enhancer.create();
        // 测试代理类
        System.out.println("-------------");
        userController.save();
        System.out.println("-------------");
        userController.delete();
        System.out.println("-------------");
        userController.update();
        System.out.println("-------------");
        userController.find();
    }
}
