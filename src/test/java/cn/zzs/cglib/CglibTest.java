package cn.zzs.cglib;

import org.junit.Test;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;

/**
 * @ClassName: CglibTest
 * @Description: 测试cglib动态代理，模拟对用户操作前进行日志记录
 * @author: zzs
 * @date: 2019年8月31日 下午4:50:06
 */
public class CglibTest {
	@Test
	public void test01() throws InterruptedException {
		// 设置输出代理类到指定路径
		System.setProperty( DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:/growUp/test" );
		// 创建Enhancer对象，用于生成代理类
		Enhancer enhancer = new Enhancer();
		// 设置哪个类需要代理
		enhancer.setSuperclass( UserController.class );
		// 设置怎么代理，这里传入的是Callback对象-MethodInterceptor父类
		LogInterceptor logInterceptor = new LogInterceptor();
		enhancer.setCallback( logInterceptor );
		// 获取代理类实例
		UserController userController = ( UserController )enhancer.create();
		// 测试代理类
		System.out.println( "-------------" );
		userController.save();
		System.out.println( "-------------" );
		userController.delete();
		System.out.println( "-------------" );
		userController.update();
		System.out.println( "-------------" );
		userController.find();
	}
}
