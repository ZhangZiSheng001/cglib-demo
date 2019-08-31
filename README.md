
# cglib
## 简介  
cglib基于asm字节码生成框架，可以动态地改造指定类的方法。与JDK的动态代理不同，cglib的代理类继承了被代理类，而不是实现被代理类的接口。  

注意：在字节码的生成和类的创建上，JDK的动态代理效率更高；在代理方法的执行效率上，由于采用了FastClass，所有cglib的效率更高。  

## 使用例子
### 需求
模拟对用户数据进行增删改时校验权限
### 工程环境
JDK：1.8.0_201  

maven：3.6.1  

IDE：Spring Tool Suites4 for Eclipse  
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
```
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



