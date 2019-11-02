
# 简介 
## 为什么会有动态代理？
举个例子，当前有一个用户操作类，要求每个方法执行前打印访问日志。  

这里可以采用两种方式：

<<<<<<< HEAD
第一种，静态代理。即通过继承原有类来对方法进行扩展。  
=======
*注意：在字节码的生成和类的创建上，JDK的动态代理效率更高；在代理方法的执行效率上，由于采用了`FastClass`，所有cglib的效率更高。*  因为JDK的动态代理中代理类中的方法是通过反射调用的，而cglib为代理类创建了FastClass对象，只要知道方法索引就可以直接调用指定方法。  
>>>>>>> refs/remotes/origin/master

当然，这种方式可以实现需求，但是当类的方法很多时，我们需要逐个添加打印日志的代码，非常繁琐。此时，如果要求加入权限校验，这个时候又需要再创建一个代理类。  

第二种，动态代理。即通过拦截器的方式来对方法进行扩展。  

动态代理只需要重写拦截器的一个方法，相比静态代理，可以减少很多代码。而且，动态代理要实现不同的代理类，只要选择不同的拦截器就可以了（可以选择多个），代理类不需要我们自己实现，可以有效实现代码解耦和可重用。

不限于以上优点，动态代理被广泛应用于日志记录、性能统计、安全控制、事务处理、异常处理等等，是spring实现AOP的重要支持。  

## 常见的动态代理有哪些？

常用的动态代理有：JDK动态代理、cglib。  

感兴趣的可以研究下`aspectJ`。  

## 什么是cglib
cglib基于`asm`字节码生成框架，用于动态生成代理类。与JDK动态代理不同，有以下几点不同：  

1. JDK动态代理要求被代理类实现某个接口，而cglib无该要求。  

2. JDK动态代理生成的代理类是该接口实现类，也就是说，不能代理接口中没有的方法，而cglib生成的代理类继承被代理类。  

3. 在字节码的生成和类的创建上，JDK的动态代理效率更高。  

4. 在代理方法的执行效率上，由于采用了`FastClass`，cglib的效率更高（以空间换时间）。  

注：因为JDK动态代理中代理类中的方法是通过反射调用的，而cglib因为引入了FastClass，可以直接调用代理类对象的方法。  

# 使用例子
## 需求
模拟对用户数据进行增删改前打印访问日志  

## 工程环境
JDK：1.8  

maven：3.6.1  

<<<<<<< HEAD
IDE：STS4  

## 主要步骤
1.  创建`Enhancer`对象：`Enhancer`是cglib代理的对外接口，以下操作都是调用这个类的方法  
2.  `setSuperclass(Class superclass)`：代理谁？  
3.  `setCallback(final Callback callback)`：怎么代理？（我们需要实现`Callback`的子接口`MethodInterceptor`，重写其中的`intercept`方法，该方法定义了代理规则)  
4.  `create()`：获得代理类
5.  使用代理类
=======
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
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD

## 创建项目
项目类型Maven Project，打包方式jar  

## 引入依赖
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


## 编写被代理类
=======
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
>>>>>>> refs/remotes/origin/master
包路径：`cn.zzs.cglib` 
<<<<<<< HEAD
这里只是简单地测试，不再引入复杂的业务逻辑。  

```java
=======
```java
/**
 * @ClassName: UserController
 * @Description: 对用户进行操作的Controller
 * @author: zzs
 * @date: 2019年8月31日 下午4:24:13
 */
>>>>>>> refs/remotes/origin/master
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
<<<<<<< HEAD

## 编写MethodInterceptor接口实现类 
包路径：`cn.zzs.cglib` 

```java
public class LogInterceptor implements MethodInterceptor {

	@Override
	public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable {
		// 设置需要代理拦截的方法
		HashSet<String> set = new HashSet<String>( 6 );
		set.add( "save" );
		set.add( "delete" );
		set.add( "update" );
		// 进行日志记录
		if( method != null && set.contains( method.getName() ) ) {
			System.out.println( "进行" + method.getName() + "的日志记录" );
		}
		// 执行被代理类的方法
		Object obj2 = proxy.invokeSuper( obj, args );
		return obj2;
	}
}
```

## 编写测试类
这里的输出代理类的class文件，方便后面分析。  

=======
### 编写测试类
>>>>>>> refs/remotes/origin/master
包路径：`test`下的`cn.zzs.cglib`  
  
```java
<<<<<<< HEAD
public class CglibTest {
	@Test
	public void test01() {
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
=======
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
>>>>>>> refs/remotes/origin/master
		userController.find();
	}
}
```
<<<<<<< HEAD

## 运行结果
	CGLIB debugging enabled, writing to 'D:/growUp/test'
=======
### 运行结果
	cn.zzs.cglib.UserController$$EnhancerByCGLIB$$e6f193aa@7bfcd12c
>>>>>>> refs/remotes/origin/master
	-------------
	进行save的日志记录
	增加用户
	-------------
	进行delete的日志记录
	删除用户
	-------------
	进行update的日志记录
	修改用户
	-------------
	查找用户

<<<<<<< HEAD
# 源码分析-获得代理类的过程
## 主要步骤
这里先简单说下过程：  

1. 根据当前Enhancer实例生成一个唯一标识key  

2. 用key去缓存中找代理类的Class实例  

3. 找到了就返回代理类实例

4. 找不到就生成后放入map，再返回代理类实例

## 获得key 
接下来具体介绍下。  

首先，一进来就先调用了`createHelper()`。  

```java
    public Object create() {
        classOnly = false;
        argumentTypes = null;
        return createHelper();
    }
```
在`createHelper()`中，创建了key，这个用于唯一标识当前类及相关配置，用于在缓存中存取代理类的Class实例。接着调用父类`AbstractClassGenerator`的`create(Object key)`方法获取代理类实例。  

```java
    private Object createHelper() {
        preValidate();
        Object key = KEY_FACTORY.newInstance((superclass != null) ? superclass.getName() : null,
                ReflectUtils.getNames(interfaces),
                filter == ALL_ZERO ? null : new WeakCacheKey<CallbackFilter>(filter),
                callbackTypes,
                useFactory,
                interceptDuringConstruction,
                serialVersionUID);
        this.currentKey = key;
        //获取代理类实例
        Object result = super.create(key);
        return result;
    }
```
在`create(Object key)`中，会调用内部类`ClassLoaderData`的`get(AbstractClassGenerator gen, boolean useCache)`方法获取代理类的Class实例。  

```java
    protected Object create(Object key) {
        try {
            ClassLoader loader = getClassLoader();
            Map<ClassLoader, ClassLoaderData> cache = CACHE;
            ClassLoaderData data = cache.get(loader);
            if (data == null) {
                synchronized (AbstractClassGenerator.class) {
                    cache = CACHE;
                    data = cache.get(loader);
                    if (data == null) {
                        Map<ClassLoader, ClassLoaderData> newCache = new WeakHashMap<ClassLoader, ClassLoaderData>(cache);
                        data = new ClassLoaderData(loader);
                        newCache.put(loader, data);
                        CACHE = newCache;
                    }
                }
            }
            this.key = key;
            //获取代理类的Class类实例
            Object obj = data.get(this, getUseCache());
            //获取代理类实例
            if (obj instanceof Class) {
                return firstInstance((Class) obj);
            }
            return nextInstance(obj);
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }
```
## 利用key从缓存中获取Class
`ClassLoaderData`有一个重要字段`generatedClasses`，是一个`LoadingCache`缓存对象，存放着当前类加载器加载的代理类的Class类实例。在以下方法中，就是从它里面寻找，通过key匹配查找。  

```java
        //这个对象存放着当前类加载器加载的代理类的Class类实例
        private final LoadingCache<AbstractClassGenerator, Object, Object> generatedClasses;
        public Object get(AbstractClassGenerator gen, boolean useCache) {
            if (!useCache) {
              return gen.generate(ClassLoaderData.this);
            } else {
              //获取代理类的Class类实例
              Object cachedValue = generatedClasses.get(gen);
              return gen.unwrapCachedValue(cachedValue);
            }
        }
```
下面重点看下`LoadingCache`这个类，需要重点理解三个字段的意思： 

```java
//K：AbstractClassGenerator 这里指Enhancer类
//KK：Object 这里指前面生成key的类
//V：Object 这里指代理类的Class类
public class LoadingCache<K, KK, V> {
    //通过key可以拿到代理类的Class实例
    protected final ConcurrentMap<KK, Object> map;
    //通过loader.apply(Enhancer实例)可以获得代理类的Class实例
    protected final Function<K, V> loader;
    //通过keyMapper.apply(Enhancer实例)可以获得key
    protected final Function<K, KK> keyMapper;
    ·······
}
```
这里通过key去map里找代理类的Class实例，如果找不到，会重新生成后放入map中。  

```java
    public V get(K key) {
        final KK cacheKey = keyMapper.apply(key);
        Object v = map.get(cacheKey);
        if (v != null && !(v instanceof FutureTask)) {
            return (V) v;
        }

        return createEntry(key, cacheKey, v);
    }
```
## 生成代理类Class
以上基本说完如何从缓存中拿到代理类实例的方法，接下来简单看下生成代理类的过程，即loader.apply(Enhancer实例)，里面的generate会生成所需的Class对象，比较复杂，后面有时间再研究吧。  
 
```java
    public Object apply(AbstractClassGenerator gen) {
        Class klass = gen.generate(ClassLoaderData.this);
        return gen.wrapCachedClass(klass);
    }
```
# 代理类代码分析
## cglib生成文件
在一开始指定的路径下，可以看到生成了三个文件，前面简介里说到在代理类的生成上，cglib的效率低于JDK动态代理，主要原因在于多生成了两个FastClass文件，至于这两个文件有什么用呢？接下来会重点分析： 
 
![cglib生成class文件](https://github.com/ZhangZiSheng001/cglib-demo/tree/master/img/cglib_generate_class.png)

## 代理类源码
本文采用`Luyten`作为反编译工具，一开始用`jd-gui`解析，但错误太多。  

下面看看代理类的源码。  

在初始化时，代理类的字段都会被初始化，这里涉及到`MethodProxy`的`create`方法。  

在实际调用update方法是会调用`MethodInterceptor`对象的`intercept`方法，执行我们自定义的代码后，最终会调用的是`MethodProxy`的`invokeSuper`方法。下面重点看看这些方法。  

注：考虑篇幅问题，这里仅展示update方法。  

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
    //被代理类update方法
    private static final Method CGLIB$update$0$Method;
    //代理类update方法
    private static final MethodProxy CGLIB$update$0$Proxy;
    private static final Object[] CGLIB$emptyArgs;
    
    static void CGLIB$STATICHOOK1() {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        //代理类Class对象
        final Class<?> forName = Class.forName("cn.zzs.cglib.UserController$$EnhancerByCGLIB$$e6f193aa");
        //被代理类Class对象
        final Class<?> forName2;
        final Method[] methods = ReflectUtils.findMethods(new String[] { "update", "()V", "find", "()V", "delete", "()V", "save", "()V" }, 
        		(forName2 = Class.forName("cn.zzs.cglib.UserController")).getDeclaredMethods());
        //初始化被代理类update方法
        CGLIB$update$0$Method = methods[0];
        //初始化代理类update方法
        CGLIB$update$0$Proxy = MethodProxy.create((Class)forName2, (Class)forName, "()V", "update", "CGLIB$update$0");
    }
    
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
## MethodProxy.create
通过以下代码可以知道，`MethodProxy`对象`CGLIB$update$0$Proxy`持有了代理类和被代理类的Class实例，以及代理方法和被代理方法的符号表示，这两个sig用于后面获取方法索引。 
 
```java
	//Class c1, 被代理对象 
    //Class c2, 代理对象 
    //String desc, 参数列表描述  
    //String name1, 被代理方法  
    //String name2,代理方法 
    public static MethodProxy create(Class c1, Class c2, String desc, String name1, String name2) {
        MethodProxy proxy = new MethodProxy();
        //创建方法签名
        proxy.sig1 = new Signature(name1, desc);
        proxy.sig2 = new Signature(name2, desc);
        //创建createInfo
        proxy.createInfo = new CreateInfo(c1, c2);
        return proxy;
    }
```

## MethodProxy.invokeSuper
以下方法中会去创建两个FastClass文件，也就是我们看到的另外两个文件。当然，它们只会创建一次。  
另外，通过原来的方法签名获得了update的方法索引。  


```java
	//传入参数obj：代理类实例
    public Object invokeSuper(Object obj, Object[] args) throws Throwable {
        try {
            //初始化，创建了两个FastClass类对象，并根据原来的方法签名得到方法索引
            init();
            //这个对象持有两个FastClass类对象和方法的索引
            FastClassInfo fci = fastClassInfo;
            //调用了代理对象FastClass的invoke方法
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
	                //helper方法用ASM框架去生成了两个FastClass类  
	                fci.f1 = helper(ci, ci.c1);  
	                fci.f2 = helper(ci, ci.c2);  
	                fci.i1 = fci.f1.getIndex(sig1);  
	                fci.i2 = fci.f2.getIndex(sig2);  
	                fastClassInfo = fci;  
	                createInfo = null;  
	            }  
	        }  
	    }  
	}
private static class FastClassInfo{  
    FastClass f1;//被代理对象FastClass  
    FastClass f2;//代理对象FastClass  
    int i1;//被代理update方法的索引  
    int i2; //代理update方法的索引 
}  
```
## FastClass.invoke
根据方法索引进行匹配，可以直接调用代理类实例的方法，而不需要像JDK动态代理一样采用反射的方式，所以在方法执行上，cglib的效率会更高。  

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
                case 4: {
                    //通过匹配方法索引，直接调用该方法
                    userController$$EnhancerByCGLIB$$e6f193aa.update();
                    return null;
                }
				·······

        }
        catch (Throwable t) {
            throw new InvocationTargetException(t);
        }
        throw new IllegalArgumentException("Cannot find matching method/constructor");
    }
```
=======
>>>>>>> refs/remotes/origin/master

> 学习使我快乐！！
