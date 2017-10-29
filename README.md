
# Shadow使用说明

### 目录
1. 概述
2. 使用SHADOW<br/>
2.1. 配置<br/>
2.1.1. pom.xml
3. WEB LISTENER切面容器<br/>
3.1	配置
4. STRUTS2 INTERCEPTOR支持<br/>
4.1	POM.XML<br/>
4.2	配置
5. SPRING INTERCEPTOR支持<br/>
5.1	POM.XML<br/>
5.2	配置
6. INSTRUMENT支持<br/>
6.1	POM.XML<br/>
6.2	使用<br/>
6.2.1 启动时<br/>
6.2.2 运行时
7. ASPECTCONFIG注解<br/>
7.1	使用<br/>
7.2	说明
8. ASPECTANNOTATIONSTRATEGY<br/>
8.1	实现<br/>
8.2	配置


### 1	概述
本文档主要记录具体业务端如何使用shadow提供的工具及服务。

### 2	使用shadow
Shadow主要由两部分组成，Core和AOP实现。Core维护了切面定义和上下文，AOP实现是基于Shadow的API协议，由各个框架AOP特性支持。
Shadow提供了对Spring、Struts2、Instrument的支持，其它框架和机制可以自行扩展。
#### 2.1	配置
##### 2.1.1	pom.xml
在你的pom.xml中加入如下依赖配置：
###### 添加dependency依赖
```xml
<!-- start shadow -->
<dependency>
    <groupId>com.dianping.orderdish</groupId>
    <artifactId>shadow-core</artifactId>
    <version>1.1.0-SNAPSHOT</version>
</dependency>
<!-- end shadow -->
```
###### shadow包含最小可用包，如无替代，请不要exclude任意jar。

### 3	Web Listener切面容器
#### 3.1	配置
在你的项目web.xml中添加如下配置即可：
```xml
<!-- shadow listener -->
<context-param>
    <param-name>shadowBasePackage</param-name>
    <param-value>com.dianping.orderdish</param-value>
    </context-param>
<listener>
    <listener-class>com.dianping.orderdish.shadow.entrance.init.ContextLoaderListener</listener-class>
</listener>
```
配置的时候注意，shadowBasePackage的值跟spring的component-scan一致，使用最大路径即可，必须配置，否则shadow不会生效。

### 4	Struts2 Interceptor支持
#### 4.1	pom.xml
```xml
<dependency>
    <groupId>com.dianping.orderdish</groupId>
    <artifactId>shadow-aspect-struts2</artifactId>
    <version>1.1.0-SNAPSHOT</version>
</dependency>
```
#### 4.2	配置
你只需要在你的Struts2配置文件中配置AspectStruts2Interceptor即可，方式如下：
```xml
<interceptors>
    <interceptor-stack name="myStack">
    <interceptor-ref name="log" />
		<interceptor-ref name="defaultStack" />
		</interceptor-stack>
</interceptors>
<default-interceptor-ref name="myStack" />
```
值得特别注意的是，AspectStruts2Interceptor要配置在第一个，否则按照Struts2的interceptor执行原理，可能直接避开了应答日志监控程序。

### 5	Spring Interceptor支持
#### 5.1 pom.xml
```xml
<dependency>
    <groupId>com.dianping.orderdish</groupId>
    <artifactId>shadow-aspect-spring</artifactId>
    <version>1.1.0-SNAPSHOT</version>
</dependency>
```
#### 5.2	配置
在你的Spring.xml中加入如下依赖配置：<br/>
这个切面需要手动设置,否则会有一些问题.例如设置 @Pointcut("execution(* *..*.*(..))") 时,被覆盖的类,Spring会为其构建一个Bean.
此时,如若这个类没有实现任意 接口 且又是 final 的,那么Bean会构建失败,导致应用无法启动,异常如下
org.springframework.aop.framework.AopConfigException: Could not generate CGLIB subclass of class [class xxx.xxx.xxx]
这里提供两个模版
##### 1.基于注解
```xml
<aop:aspectj-autoproxy/>
<bean class="com.dianping.shadow.aspect.spring.AnnotationAspectSpringInterceptor" />
```
##### 2.基于XML
```xml
<!-- 日志拦截声明bean ，此bean作为切面类使用  -->
<bean id="logInterceptor" class="com.dianping.shadow.aspect.spring.AspectSpringInterceptor"/>
<aop:config>
    <!-- 设置切面名，及切面类 -->
    <aop:aspect id="logAspect" ref="logInterceptor">
    <!-- 先设置切入点，待使用  -->
    <aop:pointcut id="logPointcut" expression="execution(public * com.dianping..service..*.*(..))"/>
    <!-- 运行前后方法配置，选择要执行的方法，参考预先设置好的切入点  -->
    <aop:around method="advice" pointcut-ref="logPointcut"/>
</aop:config>
 ```
特别需要注意的是这里只能使用```<aop:around></aop:around>```

### 6	Instrument支持
#### 6.1	pom.xml
```xml
<dependency>
    <groupId>com.dianping.orderdish</groupId>
    <artifactId>shadow-aspect-instrument</artifactId>
    <version>1.1.0-SNAPSHOT</version>
</dependency>
```
#### 6.2	使用
##### 启动时
```
java -javaagent:/Users/../shadow-1.1.0-SNAPSHOT.jar="basePackage" -cp /Users/../shadow-1.1.0-SNAPSHOT.jar com.dianping.shadow.silhouette.SilhouetteDefinition
```
+ 第一个/Users/../shadow-1.1.0-SNAPSHOT.jar是你本地Shadow的jar位置全路径
basePackage是你的扫描跟路径，例如“com.dianping.shadow” <br/>
+ 第二个/Users/../shadow-1.1.0-SNAPSHOT.jar是你需要instrument类的jar包的路径
+ com.dianping.shadow.silhouette.SilhouetteDefinition是你需要instrument的类 <br/>
***举个例子***
```
java -javaagent:/Users/*/Documents/Work/WorkSpace/shadow/shadow-aspect-instrument/target/shadow-aspect-instrument-1.1.0-SNAPSHOT.jar="com.dianping.shadow.silhouette" -cp /Users/jourrey/Documents/Work/WorkSpace/shadow/shadow-core/target/shadow-core-1.1.0-SNAPSHOT.jar com.dianping.shadow.silhouette.SilhouetteDefinition
```

##### 运行时
```
java -cp /Users/../shadow-1.1.0-SNAPSHOT.jar com.dianping.shadow.aspect.instrument.AgentAttach
```
+ /Users/../shadow-1.1.0-SNAPSHOT.jar是你本地Shadow的jar位置全路径
+ com.dianping.shadow.aspect.instrument.AgentAttach固定不变
***举个例子***
```
java -cp /Users/*/Documents/Work/WorkSpace/shadow/shadow-aspect-instrument/target/shadow-aspect-instrument-1.1.0-SNAPSHOT.jar com.dianping.shadow.aspect.instrument.AgentAttach
```
*具体也可搜javaagent用法*

### 7	AspectConfig注解
#### 7.1	使用
配置完支持的入口之后，可以开始配置切面行为。
在你需要的任何接口、类、方法上，标记@AspectConfig注解即可；
满足以下全部条件即生效：
1. 被@AspectConfig标记的对象，在shadowBasePackage覆盖范围内，无论是在自己的项目，还是在jar中；
2. 被@AspectConfig标记的接口，至少实现类，且至少有一个方法；
3. 被@AspectConfig标记的类，至少有一个方法；

#### 7.2	说明
1. annotation：需要捕捉的注解，被该注解标记的方法，将被执行切面操作，不可为 null；（继承性有注解本身@Inherited属性确定）<br/>
2. advice：被捕捉的方法执行的切面操作，不可为 null；

### 8	AspectAnnotationStrategy
shadow提供Strategy机制，可以在切面配置的Annotation找不到时，返回一个新的Annotation继续查找，不返回新Annotation表示放弃当前Strategy,跳至下一个Strategy。举个例子，例如某些pointcut被多个Annotation其中的一个标记就成立，此时不能设置多个切面，会导致执行多次，仅仅只是想配置一个切面，那么就可以使用Strategy机制。
#### 8.1	实现
实现 com.dianping.shadow.context.strategy.AspectAnnotationStrategy 接口即可
#### 8.2	配置
项目根目录“src/main”下，创建“META-INF/services/”目录，再创建“com.dianping.shadow.context.strategy.AspectAnnotationStrategy”名称的文件，文件内容是自定义Filter实现的全类名，一行一个，按顺序存放。
实现方式，请参考java的ServiceLoader
