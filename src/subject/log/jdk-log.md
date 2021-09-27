# JDKLog
2021-09-26

JDK 1.4 引入日志框架，通过 java.util.logging 包提供相关接口。暂且把 Java 自带的日志框架称为 JDKLog。

## 基础概念

### 日志流概览
都在包 java.util.logging 下。
六个关键的类：Filter、Format、Handler、Level、Logger、LoggerRecord。

应用程序调用 Logger 对象写下日志。Logger 对象组织在一个树状结构的名称空间中，子 Logger 可以从父 Logger 继承某些属性。

应用程序调用 Logger 对象写下日志，Logger 将日志信息打成 LogRecord 对象, 传给 Handler 处理。
Logger 和 Handler 对象都有支持两种日志过滤方式: Level 和 Filter(Filter 是可选的)用于决定哪些日志可以最终输出.
Handler 负责发布日志（输出到某个IO流），发布前可以先用 Formatter 对象对日志格式化(可选)。

每个 Logger 都配置若干个 Handler (包括零个)，默认 Logger 还会把日志委派一份给父 Logger，可以关闭。

某些 Handler 可以把日志传递给另一个 Handler，如此构成一条链，此时链最末端的 Handler 负责格式化日志消息。
### 日志级别
日志有级别，每个日志级别对应一个整数值，整数值越大，日志优先级越高。
### Logger
<details><summary>
客户端代码调用 Logger 对象输出日志，每个 Logger 对象有设置一个日志级别，所有低于此级别的日志，Logger 对象都会忽略。
</summary>

> As stated earlier, client code sends log requests to Logger objects. Each logger keeps track of a log level that it is interested in, and discards log requests that are below this level.
</details>

<details><summary>
Logger 是命名实体，名字使用句点分隔，由此构成树状名称空间，此名称空间一般与 java 的包名称空间一致，但不是必须的。
</summary>

> Loggers are normally named entities, using dot-separated names such as "java.awt". The namespace is hierarchical and is managed by the LogManager. The namespace should typically be aligned with the Java packaging namespace, but is not required to follow it slavishly. For example, a Logger called "java.awt" might handle logging requests for classes in the java.awt package, but it might also handle logging for classes in sun.awt that support the client-visible abstractions defined in the java.awt package.
</details>

允许创建匿名 Logger, 匿名 Logger 不在共享名称空间内。
> In addition to named Loggers, it is also possible to create anonymous Loggers that don't appear in the shared namespace. See section 1.14.

<details><summary>
每个 Logger 有一个父 Logger, 名为 "" 的 Logger 是根, 它没有父 Logger. 匿名 Logger 以根Logger为父. Logger 从父logger继承各种属性，特别地：

- 日志级别：如果 Logger 自身的级别设置为 null，则沿着树向上追溯，使用第一个不是 null 的级别作为自身的有效级别。ps：如果自己设置了，当然就使用自己的。
- handler：默认，logger 会把自己的日志传递个父 Logger 的handler，如此递归，直到根 Logger。
- 资源包名：如果logger 自身资源包名设置为 null，则沿着树向上追溯，使用第一个不是 null 的资源包名作为自身的有效资源包名。

</summary>

> Loggers keep track of their parent loggers in the logging namespace. A logger's parent is its nearest extant ancestor in the logging namespace. The root Logger (named "") has no parent. Anonymous loggers are all given the root logger as their parent. Loggers may inherit various attributes from their parents in the logger namespace. In particular, a logger may inherit:
> - Logging level. If a Logger's level is set to be null then the Logger will use an effective Level that will be obtained by walking up the parent tree and using the first non-null Level.
> - Handlers. By default a Logger will log any output messages to its parent's handlers, and so on recursively up the tree.
> - Resource bundle names. If a logger has a null resource bundle name, then it will inherit any resource bundle name defined for its parent, and so on recursively up the tree.
</details>

### 日志方法
Logger 定义了一大批日志输出方法，为了方便使用，还对每个预定义级别分别定义了对应的方法。
比如，调用 "logger.warning(..." 就比 "logger.log(Level.WARNING,..." 更方便。

定义了一堆这样的方法，它们接收类名和方法名作为额外参数，可以方便开发者快速定位问题：
`void warning(String sourceClass, String sourceMethod, String msg);`
同样定义了另一堆方法，它们直接接收日志消息，可以方便开发者使用。
`void warning(String msg);`
这第二类方法会自动尝试推断当前所处的类名和方法名，但只能大概推测一下，并不准确。尤其是上一代JVM进行了更多的优化，使得
推断类名和方法名基本不准确。
### 。。。
### LogManager
有一个全局 LogManager 对象，它维护如下信息：
- 命名 logger 的树状名称空间
- 从配置文件读取而来的日志控制属性
<details><summary>
使用静态方法 LogManager.getLogManager 可以获取这个唯一的 LogManager 对象。
ps：大意是这里提供了应用程序替换默认 LogManager 的接口。
</summary>

> There is a single LogManager object that can be retrieved using the static LogManager.getLogManager method. This is created during LogManager initialization, based on a system property. This property allows container applications (such as EJB containers) to substitute their own subclass of LogManager in place of the default class.
</details>

### 配置文件
可以通过配置文件指定日志配置，Java在启动的时候读取配置文件，要求配置文件符合标准的 java.util.Properties 格式。
也可以通过从对象中读取配置，只要它符合 LogManager API Specification 的要求，如此便可以从各种数据源获取日志配置，
比如 LDAP、JDBC 等。

这段话，认识，但是不知道什么意思？
Ans: 看看 LogManager 的描述再说？
> There is a small set of global configuration information. This is specified in the description of the LogManager class and includes a list of root-level Handlers to install during startup.

<details><summary>
初始配置可以指定特定 logger 的日志级别，此级别对该 logger 及其子 logger 生效。
</summary>

> The initial configuration may specify levels for particular loggers. These levels are applied to the named logger and any loggers below it in the naming hierarchy. The levels are applied in the order they are defined in the configuration file.
</details>

<details><summary>
初始配置可以包含任意属性，供 handler 或者负责日志的子系统使用。按约定，这属性应当以 handler 类的全限定名或者子系统的主 logger 的全限定名开头。
例如 MemoryHandler 使用属性 "java.util.logging.MemoryHandler.size" 控制内存缓冲区的大小。
</summary>

> The initial configuration may contain arbitrary properties for use by Handlers or by subsystems doing logging. By convention these properties should use names starting with the name of the handler class or the name of the main Logger for the subsystem.

> For example, the MemoryHandler uses a property "java.util.logging.MemoryHandler.size" to determine the default size for its ring buffer.
</details>

### 默认配置
JRE 附带的默认配置仅仅是默认配置，可以由厂商、系统管理员、终端用户自行修改。

默认配置仅使用有限的磁盘空间，确保不会向用户输出大量信息，也确保不会漏掉关键性的失败信息。

默认配置在根 logger 上配置了一个 handler，它把日志输出到控制台。

ps：默认配置为根 logger 设置了 INFO 级别，为根 logger 的 handler 设置了 INFO 级别。
### 动态更新配置
编程人员有多种途径在运行时调整日志配置：
- FileHandlers, MemoryHandlers, and ConsoleHandlers can all be created with various attributes.
- New Handlers can be added and old ones removed.
- New Loggers can be created and can be supplied with specific Handlers.
- Levels can be set on target Handlers.

## Level 预定义了日志级别常量
每个日志级别有一个整数值，整数值越大，日志优先级越高。
日志级别，九个常量，7个实际意义的级别。

- static Level 	OFF      Integer.MAX_VALUE      OFF is a special level that can be used to turn off logging.
- static Level 	SEVERE   10000                  SEVERE is a message level indicating a serious failure.
- static Level 	WARNING  900                    WARNING is a message level indicating a potential problem.
- static Level 	INFO     800                    INFO is a message level for informational messages.
- static Level 	CONFIG   700                    CONFIG is a message level for static configuration messages.
- static Level 	FINE     500                    FINE is a message level providing tracing information.
- static Level 	FINER    400                    FINER indicates a fairly detailed tracing message.
- static Level 	FINEST   300                    FINEST indicates a highly detailed tracing message.
- static Level 	ALL      Integer.MIN_VALUE      ALL indicates that all messages should be logged.
## 一个简单的示例
此示例使用默认配置输出日志消息。
```java
package com.wombat;
import java.util.logging.*;

public class Nose{
    // Obtain a suitable logger.
    private static Logger logger = Logger.getLogger("com.wombat.nose");
    public static void main(String argv[]) {
        // Log a FINE tracing message
        logger.fine("doing stuff");
        try{
            Wombat.sneeze();
        } catch (Exception ex) {
            // Log the exception
            logger.log(Level.WARNING, "trouble sneezing", ex);
        }
        logger.fine("done");
    }
}
```

第二个示例
1. Logger.getLogger() 方法，如果传入 null，会得到 NPE
2. 获取匿名logger，要通过 Logger.getAnonymousLogger();
3. 对于命名Logger，多次获取，都是同一个实例。
4. 对于匿名Logger，多次获取，每次都是新的实例。
```java
package cc.xizhan.jdklog;

import java.util.logging.Logger;

/**
 * <p> Description: 获取Logger对象</p>
 */
public class LoggerDemo2 {
    public static void main(String[] args) {
        // 获取匿名logger对象
        {
            // Exception in thread "main" java.lang.NullPointerException
            // Logger logger = Logger.getLogger(null);

            Logger logger = Logger.getAnonymousLogger();
            Logger logger2 = Logger.getAnonymousLogger();
            System.out.println(logger == logger2);
            // false

        }

        // 获取常规 logger
        {
            Logger logger = Logger.getLogger("hello");
            Logger logger2 = Logger.getLogger("hello");
            System.out.println(logger == logger2);
            // true
        }
    }
}
```

## Q
- Q. JDKLog 是线程安全的吗？ \
  Ans: 是的。在 Logger 类的API 文档中有说明。
  >  All methods on Logger are multi-thread safe.
- Q. Logger.logp() 和 log 有什么区别？

- INFO: 确定了，JDK Log 的日志消息不支持占位符。

## 附：Logger 的日志输出方法
最基本的，自然是 log() 方法，共重载了七个。
最常用的可能是 log(level, msg) 和 log(level, throwable);
```
- void 	log(Level level, String msg)
    Log a message, with no arguments.
- void 	log(Level level, String msg, Object param1)
    Log a message, with one object parameter.
- void 	log(Level level, String msg, Object[] params)
    Log a message, with an array of object arguments.
- void 	log(Level level, String msg, Throwable thrown)
    Log a message, with associated Throwable information.
- void 	log(Level level, Supplier<String> msgSupplier)
    Log a message, which is only to be constructed if the logging level is such that the message will actually be logged.
- void 	log(Level level, Throwable thrown, Supplier<String> msgSupplier)
    Log a lazily constructed message, with associated Throwable information.
- void 	log(LogRecord record)
    Log a LogRecord.
```

然后，对于预定义级别，提供了几个快捷方法，相比于完整的 log 方法，这些快捷方法都只有两个重载模式，最长用的就是下面这种。
```
- void severe(msg)
- void warning(msg)
- void info(msg)
- void config(msg)
- void fine(msg)
- void finer(msg)
- void finest(msg)
```
另一个重载模式是 `void xxx(Supplier<String> msgSupplier)`

还有几个特别的快捷方法，entering(), exiting(), throwing().
```
- void 	entering(String sourceClass, String sourceMethod)
        Log a method entry.
- void 	entering(String sourceClass, String sourceMethod, Object param1)
        Log a method entry, with one parameter.
- void 	entering(String sourceClass, String sourceMethod, Object[] params)
        Log a method entry, with an array of parameters.
- void 	exiting(String sourceClass, String sourceMethod)
        Log a method return.
- void 	exiting(String sourceClass, String sourceMethod, Object result)
        Log a method return, with result object.

- void 	throwing(String sourceClass, String sourceMethod, Throwable thrown)
        Log throwing an exception.
```

## 参考
[Java Logging Overview](https://chenxizhan.top/docs/jdk8/technotes/guides/logging/overview.html)
