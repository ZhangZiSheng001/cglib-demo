
# cglib

## 简介  
cglib基于`asm`字节码生成框架，可以动态地改造指定类的方法，是spring实现AOP的重要支持。与JDK的动态代理不同，cglib的代理类继承了被代理类，而不是实现被代理类的接口。  

*注意：在字节码的生成和类的创建上，JDK的动态代理效率更高；在代理方法的执行效率上，由于采用了`FastClass`，所有cglib的效率更高。*  因为JDK的动态代理中代理类中的方法是通过反射调用的，而cglib为代理类创建了FastClass对象，只要知道方法索引就可以直接调用指定方法。  

## 使用例子
### 需求
模拟对用户数据进行增删改时校验权限
### 工程环境
JDK：1.8.0_201  

maven：3.6.1  

IDE：Spring Tool Suites4 for Eclipse  

### 代理的主要步骤
创建`Enhancer`对象：`Enhancer`是cglib代理的对外窗口，以下都是调用这个类的方法  
`setSuperclass(Class superclass)`：设置被代理的类  
`setCallback(final Callback callback)`：设置被代理类如何增强，这个`Callback`对象需要我们创建实现类，实现的接口是`MethodInterceptor`（是`Callback`的子接口）  
`create()`：获得代理类


### 创建项目
项目类型Maven Project，打包方式jar
### 引入依赖
		<!-- cglib -->
		<dependency>
			<groupId>cglib</groupId>
			<artifactId>cglib</artifactId>
			<version>3.2.5</version>
		</dependency>
		<!-- junit -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.12</version>
			<scope>test</scope>
		</dependency>
### 编写MethodInterceptor实现类
首先实现MethodInterceptor对类的方法进行改造。即设置类的方法如何增强？  
包路径：`cn.zzs.cglib` 

```java
public class PermissionCheckInterceptor implements MethodInterceptor {
	//设置类应该如何增强？
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		//设置需要拦截的方法
		HashSet<String> set = new HashSet<String>(6);
		set.add("save");
		set.add("delete");
		set.add("update");
		//进行权限校验
		if(method != null && set.contains(method.getName())) {
			System.out.println("进行权限校验");
		}
		//再执行被代理类的方法，这个执行过程使用了FastClass，相比JDK的动态代理执行效率更高
		Object obj2 = proxy.invokeSuper(obj, args);
		return obj2;
	}
}
```
### 编写被代理类
包路径：`cn.zzs.cglib` 
```java
/**
 * @ClassName: UserController
 * @Description: 对用户进行操作的Controller
 * @author: zzs
 * @date: 2019年8月31日 下午4:24:13
 */
public class UserController {
	public void save() {
		System.out.println("增加用户");
	}
	public void delete() {
		System.out.println("删除用户");
	}
	public void update() {
		System.out.println("修改用户");
	}
	public void find() {
		System.out.println("查找用户");
	}
}
```
### 编写测试类
包路径：`test`下的`cn.zzs.cglib`  
  
```java
/**
 * @ClassName: CglibTest
 * @Description: 测试cglib动态代理，模拟对用户操作前进行权限校验
 * @author: zzs
 * @date: 2019年8月31日 下午4:50:06
 */
public class CglibTest {
    @Test
	public void test01() {
	//创建PermissionCheckInterceptor实现类对象
	PermissionCheckInterceptor permissionCheckInterceptor = new PermissionCheckInterceptor();  
	//创建Enhancer对象，用于方法增强  
	Enhancer enhancer = new Enhancer();  
	//设置哪个类的方法需要增强？  
	enhancer.setSuperclass(UserController.class);    
	//设置方法如何增强？这里传入的是Callback对象-MethodInterceptor父类 
	enhancer.setCallback(permissionCheckInterceptor); 
	//获取代理类实例
	UserController userController = (UserController) enhancer.create();
	System.out.println(userController);
	//测试增强后的结果  
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
```
### 运行结果
	cn.zzs.cglib.UserController$$EnhancerByCGLIB$$e6f193aa@7bfcd12c
	-------------
	进行权限校验
	增加用户
	-------------
	进行权限校验
	删除用户
	-------------
	进行权限校验
	修改用户
	-------------
	查找用户


> 学习使我快乐！！
