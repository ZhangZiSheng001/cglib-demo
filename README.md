# 什么是cglib

简单来说，cglib 就是用来生成代理类的。与 JDK 自带的动态代理相比，有以下几点不同： 

1. JDK 动态代理要求被代理类实现某个接口，而 cglib 无该要求。 

2. 在代理类的生成速度方面，JDK 的动态代理更快。但是，在目标方法的执行方面，由于采用了`FastClass`，cglib 的效率更高（以空间换时间，后面会讲到）。 

# 为什么要用动态代理？

学习任何一个技术，需要思考它是用来解决什么问题的，甚至思考不用它行不行。这里，我们需要先思考为什么要使用动态代理。

为了更好地解答这个问题，这里用一个简单的例子来说明。

我有这么一个用户相关的 Controller。

```java
class UserController {
	public Response create(UserCreateDTO dto){
		String id = userService.create(dto);
		return Response.of(id);
	}
	
	public Response update(UserUpdateDTO dto){
		String id = userService.update(dto);
		return Response.of(id);
	}
	
    public Response delete(UserDeleteDTO dto){
		String id = userService.delete(dto);
		return Response.of(id);
	}
	
	public Response getById(String id){
		UserVO user = userService.getById(id);
		return Response.of(user);
	}
	
	// ······
}
```

为了方便监控和跟踪，我希望打印每个方法的入参、出参、当前登录人等等信息。简单的做法就是直接在每个方法里嵌入打印日志的代码，如下：

```java
class UserController {
	public Response create(UserCreateDTO dto){
		// 打印入参日志
		// ······
		Response response = Response.of(userService.create(dto));
		// 打印出参日志
		// ······
		return response;
	}
	
	public Response update(UserUpdateDTO dto){
		// 打印入参日志
		// ······
		Response response = Response.of(userService.update(dto));
		// 打印出参日志
		// ······
		return response;
	}
	
    public Response delete(UserDeleteDTO dto){
		// 打印入参日志
		// ······
		Response response = Response.of(userService.delete(dto));
		// 打印出参日志
		// ······
		return response;
	}
	
	public Response getById(String id){
		// 打印入参日志
		// ······
		Response response = Response.of(userService.getById(id));
		// 打印出参日志
		// ······
		return response;
	}
	
	// ······
}
```

可以看到，这种做法有两个的问题：**一是需要手动添加大量重复代码，二是代码耦合度较高**。

当然问题要一个个解决，首先，针对第二个问题，我们创建了一个`UserControllerCommonLogProxy`来专门处理请求日志，如下：

```java
class UserControllerCommonLogProxy extends UserController {
	public Response create(UserCreateDTO dto){
		// 打印入参日志
		// ······
		Response response = super.create(dto);
		// 打印出参日志
		// ······
		return response;
	}
	
	public Response update(UserUpdateDTO dto){
		// 打印入参日志
		// ······
		Response response = super.update(dto);
		// 打印出参日志
		// ······
		return response;
	}
	
    public Response delete(UserDeleteDTO dto){
		// 打印入参日志
		// ······
		Response response = super.delete(dto);
		// 打印出参日志
		// ······
		return response;
	}
	
	public Response getById(String id){
		// 打印入参日志
		// ······
		Response response = super.getById(id);
		// 打印出参日志
		// ······
		return response;
	}
	
	// ······
}
```

上面例子中，我们不直接访问`UserController`，而是通过`UserControllerCommonLogProxy`来间接访问。其实，这就是代理，严格划分的话属于**静态代理**，和我们接下来要讲的动态代理不大一样。

静态代理解决了代码耦合的问题，但这种做法产生了一个新的问题：**需要手动创建和维护大量的代理类**。我需要为每一个`Controller`都增加一个`Proxy`，而且，当`UserController`增加方法时，我们也需要在对应的`Proxy`中实现。

这个时候，我们会想，要是代理类能自动生成该多好。于是，动态代理就派上用场了。

我们只要指定好代理类的逻辑，动态代理就能帮我们生成对应的代理类（可以在编译时生成，也可以在运行时生成），而不需要我们手动创建。下面的这段代码就是在定义代理类的逻辑。

```java
public class LogInterceptor implements MethodInterceptor {
    @Override
    public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable {
        // 打印入参
       	// ······
        // 执行目标类的方法
        Object obj2 = proxy.invokeSuper( obj, args );
        // 打印出参
        // ······
        return obj2;
    }
}
```

运行对应的方法，代理类自动生成。具体使用方法后面会讲。

![cglib生成class文件](https://img2018.cnblogs.com/blog/1731892/201911/1731892-20191123120909418-874653881.png)

所以，我们用动态代理，本质上是为了更简单方便地实现 AOP。

# 常见的动态代理有哪些？

我们接触比较多的一般是 JDK 动态代理和本文讲到的 cglib，这两个类库都是运行时生成代理类。spring-aop 同时使用了这两种类库。

另外，还有`javassit`和`aspectJ`等第三方类库， 它们既能编译时生成代理类，也能在运行时生成代理类。本文不作扩展，感兴趣的可以研究下。

# 如何使用cglib

## 需求

还是继续开篇的例子，我需要打印`UserController`的入参和出参。

## 工程环境

JDK：1.8

maven：3.6.1 

IDE：STS4 

## 引入依赖

项目类型Maven Project，打包方式jar 

```xml
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
```

## 定义代理类的逻辑

需要注意一点，从性能方面考虑，建议使用`proxy.invokeSuper`来调用目标类的方法，而不是使用`method.invoke`，后面会解释原因。

```java
public class LogInterceptor implements MethodInterceptor {

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.err.println("打印" + method.getName() + "方法的入参");
        // 注意，调用proxy.invokeSuper比method.invoke更快
        Object obj2 = proxy.invokeSuper(obj, args);
        System.err.println("打印" + method.getName() + "方法的出参");
        return obj2;
    }
}
```

## 获取代理类

获取代理类的过程挺好理解的，我们需要告诉 cglib，我要代理谁，如何代理。

```java
    @Test
    public void test01() throws InterruptedException {
        // 设置输出代理类到指定路径，便于后面分析
        // System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:/growUp/test");
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
```

这里我们也可以同时设置多个`Callback`，如果设置了多个`Callback`，需要再设置`CallbackFilter`来指定每个方法使用的是哪个`Callback`。

## 运行结果

```
-------------
打印save方法的入参
增加用户
打印save方法的出参
-------------
打印delete方法的入参
删除用户
打印delete方法的出参
-------------
打印update方法的入参
修改用户
打印update方法的出参
-------------
打印find方法的入参
查找用户
打印find方法的出参
```

# 代理类源码分析

cglib 如何生成代理类的源码这里就不分析了（我们只要记住两点，1. cglib 的代理类会缓存起来，不会重复创建；2. 使用的是 asm 来生成`Class`文件），我们直接来看生成的代理类源码。

## 代理类文件

在上面例子中生成了三个文件，一个代理类文件，两个`FastClass`文件，通过 debug 可以发现，代理类文件是调用`Enhancer.create`的时候生成的，而两个`FastClass`文件是第一次调用`MethodProxy.invokeSuper`的时候才生成。这两个`FastClass`是 cglib 代理类方法执行快的关键。

![zzs_cglib_01](https://img2018.cnblogs.com/blog/1731892/201911/1731892-20191123120909418-874653881.png)

## 代理类源码

下面看看代理类文件的源码（本文采用`Luyten`作为反编译工具）。 

在静态代码块执行时，会初始化目标类方法对应的`Method`对象，也会初始化每个`Method`对应的`MethodProxy`，我们需要关注下`MethodProxy.create`方法。

另外，我们需要注意两个方法，一个是`update`方法，该方法中会去调用我们定义的`MethodInterceptor`的`intercept`方法，另一个是`CGLIB$update$0`方法，该方法直接调用`UserController`的`update`方法。后面我们会发现，`update`方法最终会调用`CGLIB$update$0`方法，这就是 cglib 不使用反射而直接调用目标方法的关键。

注：考虑篇幅问题，这里仅展示 update 方法。 

```java
//生成类的名字规则是：被代理classname + "$$"+classgeneratorname+"ByCGLIB"+"$$"+key的hashcode
public class UserController$$EnhancerByCGLIB$$e6f193aa extends UserController implements Factory {
    private boolean CGLIB$BOUND;
    public static Object CGLIB$FACTORY_DATA;
    private static final ThreadLocal CGLIB$THREAD_CALLBACKS;
    private static final Callback[] CGLIB$STATIC_CALLBACKS;
    
    //我们一开始传入的MethodInterceptor对象
    private MethodInterceptor CGLIB$CALLBACK_0;
    private static Object CGLIB$CALLBACK_FILTER;
    //目标类的update方法对象
    private static final Method CGLIB$update$0$Method;
    //代理类的update方法对象
    private static final MethodProxy CGLIB$update$0$Proxy;
    private static final Object[] CGLIB$emptyArgs;
    
    static void CGLIB$STATICHOOK1() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        final Class<?> forName = Class.forName("cn.zzs.cglib.UserController$$EnhancerByCGLIB$$e6f193aa");
        final Class<?> forName2;
        final Method[] methods = ReflectUtils.findMethods(new String[] { "update", "()V", "find", "()V", "delete", "()V", "save", "()V" }, 
                (forName2 = Class.forName("cn.zzs.cglib.UserController")).getDeclaredMethods());
        //初始化目标类的update方法对象
        CGLIB$update$0$Method = methods[0];
        //初始化代理类update方法对象
        CGLIB$update$0$Proxy = MethodProxy.create((Class)forName2, (Class)forName, "()V", "update", "CGLIB$update$0");
    }
    // 这个方法将直接调用UserController的update方法
    final void CGLIB$update$0() {
        super.update();
    }
    
    public final void update() {
        MethodInterceptor cglib$CALLBACK_2;
        MethodInterceptor cglib$CALLBACK_0;
        if ((cglib$CALLBACK_0 = (cglib$CALLBACK_2 = this.CGLIB$CALLBACK_0)) == null) {
            CGLIB$BIND_CALLBACKS(this);
            cglib$CALLBACK_2 = (cglib$CALLBACK_0 = this.CGLIB$CALLBACK_0);
        }
        //一般走这里，即调用我们传入MethodInterceptor对象的intercept方法
        if (cglib$CALLBACK_0 != null) {
            cglib$CALLBACK_2.intercept((Object)this, UserController$$EnhancerByCGLIB$$e6f193aa.CGLIB$update$0$Method, UserController$$EnhancerByCGLIB$$e6f193aa.CGLIB$emptyArgs, UserController$$EnhancerByCGLIB$$e6f193aa.CGLIB$update$0$Proxy);
            return;
        }
        super.update();
    }
```

## 创建FastClass文件

在`MethodProxy.invokeSuper(Object, Object[])`方法中，我们会发现，两个`FastClass`文件是在`init`方法中生成的。当然，它们也只会创建一次，并且作为单例使用。我们用到的主要是代理类的`FastClass`，通过它，我们可以直接调用到`CGLIB$update$0`方法，相当于可以直接调用目标类的`update`方法。

```java
    public Object invokeSuper(Object obj, Object[] args) throws Throwable {
        try {
            //初始化，创建了两个FastClass类对象
            init();
            FastClassInfo fci = fastClassInfo;
            // 这里将直接调用代理类的CGLIB$update$0方法，而不是通过反射调用
            // fci.f2：代理类的FastClass对象，fci.i2为CGLIB$update$0方法对应的索引，obj为当前的代理类对象，args为update方法的参数列表
            return fci.f2.invoke(fci.i2, obj, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    private void init(){  
        if (fastClassInfo == null){  
            synchronized (initLock){  
                if (fastClassInfo == null){  
                    CreateInfo ci = createInfo;  
                    FastClassInfo fci = new FastClassInfo();  
                    // 创建代理类的FastClass对象
                    fci.f1 = helper(ci, ci.c1);  
                    // 创建目标类的FastClass对象
                    fci.f2 = helper(ci, ci.c2);  
                    // 获取update方法的索引
                    fci.i1 = fci.f1.getIndex(sig1);  
                    // 获取CGLIB$update$0方法的索引，这个很重要
                    fci.i2 = fci.f2.getIndex(sig2);  
                    fastClassInfo = fci;  
                    createInfo = null;  
                }  
            }  
        }  
    }
```
## cglib代理方法快的秘密

打开代理类的`FastClass`文件，可以看到，通过方法索引我们可以匹配到`CGLIB$update$0`方法，并且直接调用它。

```java
    //传入参数：
    //n：方法索引
    //o：代理类实例
    //array：方法输入参数
    public Object invoke(final int n, final Object o, final Object[] array) throws InvocationTargetException {
        final UserController$$EnhancerByCGLIB$$e6f193aa userController$$EnhancerByCGLIB$$e6f193aa = (UserController$$EnhancerByCGLIB$$e6f193aa)o;
        try {
            switch (n) {
                case 0: {
                    return new Boolean(userController$$EnhancerByCGLIB$$e6f193aa.equals(array[0]));
                }
                case 1: {
                    return userController$$EnhancerByCGLIB$$e6f193aa.toString();
                }
                case 2: {
                    return new Integer(userController$$EnhancerByCGLIB$$e6f193aa.hashCode());
                }
                case 3: {
                    return userController$$EnhancerByCGLIB$$e6f193aa.clone();
                }
                // ·······
                case 24: {
                    // 通过匹配方法索引，直接调用该方法，这个方法将直接调用代理类的超类的方法
                    userController$$EnhancerByCGLIB$$e6f193aa.CGLIB$update$0();
                    return null;
                }
                // ·······

        }
        catch (Throwable t) {
            throw new InvocationTargetException(t);
        }
        throw new IllegalArgumentException("Cannot find matching method/constructor");
    }
```

以上基本分析完 cglib 代理类方法的执行过程，并且解释了代理类方法执行快的原因。

最后，感谢阅读。

> 2021-04-26更改

> 相关源码请移步：https://github.com/ZhangZiSheng001/cglib-demo

> 本文为原创文章，转载请附上原文出处链接：https://www.cnblogs.com/ZhangZiSheng001/p/11917086.html