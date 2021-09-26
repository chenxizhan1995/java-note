# Tomcat 中的日志
2021-09-26
<details><summary>
Tomcat 内部使用的日志框架叫做 JULI。它是从 commons logging 派生出来的。
JULI 框架以硬编码的方式在底层使用 java.util.logging 日志框架。这样就能确保 tomcat
的内部日志和任意 webapp 的日志互相独立（即使webapp中使用
commons logging 管理日志）。
ps：意思是 Tomcat 和 webapp 的日志可以独立管理，互不干扰。
</summary><blockquote>
The internal logging for Apache Tomcat uses JULI, a packaged renamed fork of
Apache Commons Logging that is hard-coded to use the java.util.logging framework.
This ensures that Tomcat's internal logging and any web application logging will
remain independent, even if a web application uses Apache Commons Logging.
</blockquote></details>

可以调整 Tomcat 的内部日志配置（参考Tomcat官网文档），但一般不必要。

运行于 Tomcat 之上的 webapp，管理日志的方式可以是：
- 自主选择日志框架
- 使用系统日志API，`java.util.logging`.
- 使用 Java Servlet 规范中定义的日志 API：`javax.servlet.ServletContext.log(...)`

<details><summary>
不同的 webapp 使用的日志框架互相独立，但 java.util.logging 日志框架例外，如果
webapp 的日志库直接或者间接使用了它，那它的元素将会在 webapp 之间共享，因为这个
日志框架是由系统类加载器加载的。
Q. `elements of it will be shared across web applications` 这句话，意味着什么？

</summary><blockquote>
The logging frameworks used by different web applications are independent. See
class loading for more details. The exception to this rule is java.util.logging.
If it is used directly or indirectly by your logging library then elements of it
will be shared across web applications because it is loaded by the system class
loader.
</blockquote></details>

## 控制台
在 Unix 系统上，tomcat 会把控制台输出重定向到 catalina.out 文件中，文件名可以配置。
在 Windows 上，作为服务运行时，Tomcat 也会把控制台输出重定向到文件中，但文件名不同。
注：控制台输出，包括 stdout 和 stderr 都会重定向。

Tomcat 的默认日志配置会把同一条信息向控制台和日志文件各输出一次，开发时这样挺好，
生产环境就不合适了。
> The default logging configuration in Apache Tomcat writes the same messages to
the console and to a log file. This is great when using Tomcat for development,
but usually is not needed in production.

## java.util.logging (default)

<details><summary>
JDK 自带的 java.util.logging 实现功能不够用，对于 Tomcat 来说，主要问题是不支持
各个 webapp 的日志独立配置。所以，默认情形下，Tomcat 使用自己的名为 JULI 的实现
替换了 JDK 自带的 LogManager 实现。
</summary>
The default implementation of java.util.logging provided in the JDK is too limited to be useful. The key limitation is the inability to have per-web application logging, as the configuration is per-VM. As a result, Tomcat will, in the default configuration, replace the default LogManager implementation with a container friendly implementation called JULI, which addresses these shortcomings.
</details>
JULI 与默认的 java.util.logging 实现支持同样的配置机制。主要区别是可以为每个
classloader 独立提供配置文件，并扩展了一些配置语法，使其更灵活。

JULI 是默认启用的，并在常规全局的 java.util.logging 配置之上支持针对每个 classloader
的配置。于是可以在如下级别配置日志：
- 全局。在文件 `${catalina.base}/conf/logging.properties` 中配置。
  此文件由系统属性 `java.util.logging.config.file` 指定，tomcat 的启动脚本设置了此属性，使其
  指向  `${catalina.base}/conf/logging.properties`。当此文件不存在或者不可读时，会回到
  JRE 下的  ${java.home}/lib/logging.properties 作为配置。
- webapp。文件 `WEB-INF/classes/logging.properties`

Q. 既然在使用 java.util.logging 时，元素是共享的，那么各个 webapp 独立的配置文件有什么作用？

Q. 使用 java.util.logging 记录框架，到底是共享的还是独立的？

JRE 的默认配置，使用 ConsoleHandler 把日志输出到 stderr，Tomcat 的默认配置还增加了
AsyncFileHandlers 处理器把日志输出到文件。

处理器的默认级别是 INFO，其它可用级别有： SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL.

要更改 Tomcat 内部日志级别为 DEBUG，需要同时调整适当的 handler 和 logger 的日志级别。
```
org.apache.catalina.session.level=ALL
java.util.logging.ConsoleHandler.level=ALL
```
若要开启 DEBUG 日志级别，建议尽可能小的限制其范围（模块、包、类），因为 DEBUG 日志信息太多了。

PS：再往后的，先了解 JDKLog 的知识，更容易看懂。

JULI 对日志配置语法做了一些扩展，主要区别有：
- 可以对 handler 名字添加前缀，从而可以为同一个 handler 类创建多个实例。前缀以数字开头，以句点结尾，例如 `22foobar.`
- 配置项的值支持系统属性替换，例如 `${systemPropertyName}`
- 如果使用的类加载器实现了 org.apache.juli.WebappProperties 接口（Tomcat 的webapp的类加载器实现了这个接口），那么有如下
  三个额外系统属性可用，${classloader.webappName}, ${classloader.hostName}, ${classloader.serviceName}，他们分别代表
  webapp 的名字，主机名，服务名。
- 默认，当 logger 有自己的 handler 时，不会把日志向父日志器传递，但可以配置，布尔属性 loggerName.useParentHandlers。
- root logger 的处理器，用 .handlers 属性配置。
- 默认，日志文件保留 90 天。可以针对每个 handler 配置最大保留天数， handlerName.maxDays 属性，指定 `<= 0`的值表示永久保留。

还提供了其它实现类，值得一提的是 org.apache.juli.FileHandler 和 org.apache.juli.AsyncFileHandler.
## 生产环境的考虑
You may want to take note of the following:

- Consider removing ConsoleHandler from configuration. By default (thanks to the .handlers setting) logging goes both to a FileHandler and to a ConsoleHandler. The
  output of the latter one is usually captured into a file, such as catalina.out. Thus you end up with two copies of the same messages.
- Consider removing FileHandlers for the applications that you do not use. E.g., the one for host-manager.
- The handlers by default use the system default encoding to write the log files. It can be configured with encoding property. See Javadoc for details.
- Consider configuring an Access log.

## 参考
[Apache Tomcat 9 (9.0.53) - Logging in Tomcat](https://tomcat.apache.org/tomcat-9.0-doc/logging.html)
