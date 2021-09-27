# servlet 4.0
2021-09-27

# 前言
……
## 目标读者
- servlet 容器厂商
- servlet 开发工具厂商
- 高级servlet开发者

特别强调，此文档不适合用作 servlet 开发的指南。

## API 参考
servlet 相关的api 文档，在 http://docs.oracle.com/javaee/ 网站上。

## ……
# 1. 概览
## 1.1 Servlet是什么？

Servlet 是一个基于java技术的web组件，由 servlet 容器管理，用于生成动态内容。
因为是基于 Java 技术，故而是平台无关的，可以运行在任何支持 Java 技术的 web 服务器
上。servlet 容器，也叫 servlet 引擎，是一种 web 服务器扩展，用以提供 servlet 功能。
servlet 使用 request/response 范例（paradigm）与 web 客户端交互。

## 1.2 Servlet 容器是什么？

servlet 容器是 web 服务器或者应用服务器的一部分，能提供网络服务——接收网络请求、编解码数据、发送
响应，同时负责管理 servlet 生命周期。
ps：这里是网络服务，并不仅限于 HTTP 请求，所以 Servlet 是 HTTPServlet 的父类的父类。

> The servlet container is a part of a Web server or application server that provides the
network services over which requests and responses are sent, decodes MIME-based
requests, and formats MIME-based responses. A servlet container also contains and
manages servlets through their lifecycle.

容器必须支持 HTTP 协议，可选择性地支持 HTTPS 协议。必须支持 HTTP/1.1 和 HTTP/2。
在支持 HTTP/2 的时候，必须支持 h2 和 h2c 协议头标识符。

servlet 4.0 要求容器所支持的最低 JDK 版本为 1.8。
## 1.3 一个示例
典型的请求处理过程如下：
1. 客户端发起http请求
2. web 服务器收到请求，转交给 servlet 容器。容器可以与 web 服务器是同一个进程，可以是同一个主机上的不同进程，
  也可以是不同主机上的进程。
3. servlet 容器根据其管理的 servlet 的配置选择合适的 servlet，把调用 servlet 处理请求，调用时传入 request
  和 resposne 对象。
4. servlet 从 request 中获取所有需要的数据，执行任何适当的业务逻辑，然后把响应数据通过 response 对象返回。
5. servlet 处理请求之后，servlet 容器负责确保响应数据正确刷新，并把控制权交回给 web 服务器。

## 1.4 Servlet 和其它技术对比
servlet 的抽象程度比 CGI 更高，但比 Web 框架更低。

优点如下：
- 比 CGI 高效很多
- 定义了标准接口，故许多web服务器都支持
- 依托于 java 平台，可以充分利用 java 平台的优势

## 1.5 和 JavaEE 的关系

Sevlet 4.0 是 Java EE 8 必须的组件。当把 Servlet 容器和 Servlet 部署于 Java EE
中时，必须满足额外的要求。

## 1.6 与 Java Servlet 规范 2.5 的兼容性

### 1.6.1 处理注解
主要是 metadata-complete 元素和 @webservlet注解之间的关系。

要求说是 metadata-complete 元素的效果不应受 web.xml 版本的影响，只看 servlet
容器实现的 servlet 规范的版本。

PS： metadata-complete 元素。
> Servlet 3.0 的部署描述文件 web.xml 的顶层标签 `<web-app>` 有一个 metadata-complete 属性，
该属性指定当前的部署描述文件是否是完全的。如果设置为 true，则容器在部署时将只依赖部署描述文件，
忽略所有的注解（同时也会跳过 web-fragment.xml 的扫描，亦即禁用可插性支持）；
如果不配置该属性，或者将其设置为 false，则表示启用注解支持（和可插性支持）
# 2. Servlet 接口
Servlet 接口是 Java Servlet API 的核心抽象。所有 Servlet 或直接或间接地实现这个接口（更多是间接）。
Java Servlet API 提供了此接口的两个实现类，GenericServlet 和 HttpServlet，开发者通常
通过继承 HttpServlet 编写自己的 Servlet。
## 2.1 请求处理方法
基础接口 Servlet 定义了 service 方法处理客户端请求。对于某特定 Servlet 实例，每
分配给它一个请求都对应调用一次 service 方法。

Servlet 开发者要确保 service 方法多线程下的线程安全性。
### 2.1.1 特定于 HTTP 的请求处理方法
抽象类 HttpServlet 在 Servlet 接口的的基础上做了定制，以辅助处理基于 HTTP 协议的请求。
它增添了如下方法，这些方法会在 service 方法中自动选择合适的调用。
- doGet()
- doPost()
- doPut()
- doDelete()
- doHead()
- doOptions()
- doTrace()

一般，基于 HTTP 协议的 Servlet 开发者只需要关注 doGet() 和 doPost() 方法。其余方法
意图给非常熟悉 HTTP 协议的开发人员使用。
### 2.1.2 额外的方法
介绍其它HTTP方法

> The doPut and doDelete methods allow Servlet Developers to support HTTP/1.1
clients that employ these features. The doHead method in HttpServlet is a
specialized form of the doGet method that returns only the headers produced by the
doGet method. The doOptions method responds with which HTTP methods are
supported by the servlet. The doTrace method generates a response containing all
instances of the headers sent in the TRACE request.


不支持 CONNECT 方法，因为此方法是给代理服务用的，而 Servlet 的定位是终端服务器。
### 2.1.3 条件 GET 支持
HttpServlet 定义了 getLastModified 方法，以支持条件GET请求（自动在 service方法内实现的）。
这在某些情况写可以显著节省带宽。
ps：getLastModified() 需要开发者自行实现。如果未实现，则总是返回 -1.

## 2.2 实例数量
servlet 声明决定了 Servlet 实例数量。servlet 声明通过注解或者在 web.xml 文件中定义。

对于非分布式环境（默认情形），servlet 容器需确保每个 servlet 声明仅对应一个servlet实例。
但若 servlet 实现了SingleThreadModel接口，则可以创建多个实例以处理大负载。

对于分布式环境，容器需确保每个每个 servlet 声明在每个 JVM 中仅对应一个实例。但若
servlet 实现了 SingleThreadModel 接口，则可以对应多个实例。
### 2.2.1 关于单线程模型的说明
实现 SingleThreadModel 接口，容器将确保任意时刻至多有一个线程指定给定 servlet 的
service 方法。需要注意的是，仅仅是 servlet 实例，对于其它对象，比如 HttpSession 实例
不受此约束，从而可能有多个 servlet 同时访问此对象。

建议开发者使用其它方案解决此类问题，而不是使用 SingleThreadModel 接口，比如同步代码块。
 SingleThreadModel 接口在此规范中以标记为过时。
## 2.3 servlet 生命周期
对 servlet 定义了良好的生命周期：载入、实例化、提供服务、卸载。生命周期在API中体现为
Servlet 接口的 init()、service()、destroy() 方法，任何 servlet 都必须直接或间接
实现这三个方法。

### 2.3.1 载入和实例化
servlet 容器负责载入和实例化 servlet。载入和实例化可以在容器启动时执行，也可以推迟
到第一个需要 servlet 处理的请求到来时执行。
### 2.3.2 初始化
容器创建 servlet 实例之后，必须在响应请求之前对 servlet 初始化。初始化就是调用
servlet 的 init 方法。servlet 可以在此方法内执行一些耗时操作、初始化操作、一次性
任务，等等。容器调用 init 方法的时候需要提供一个实现了 ServletConfig 接口的对象，
此对象相对于每个 servlet 声明唯一。servlet 可从此对象获取 name-value 初始配置，
还可以从此对象获取 ServletContext 对象，它代表了 servlet 的运行时环境。

> The container initializes
the servlet instance by calling the init method of the Servlet interface with a
unique (per servlet declaration) object implementing the ServletConfig interface.

#### 2.3.2.1 初始化时出错
在初始化时，servlet 可以抛出 UnavailableException 或者 ServletException，表示
初始化失败，这时容器必须释放 servlet 实例，容器不会调用 destroy 方法，因为毕竟
servlet 未完全初始化。

初始化失败后，容器可以根据需要再次创建servlet实例并尝试初始化，唯一的例外是
UnavailableException 中设置了等待时间，则容器必须在指定时间段之后才能再次实例
化和初始化此 servlet。

写了个demo，确实好玩。
```java
package cc.xizhan.demo.servlethello;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * <p> Description: </p>
 */
@WebServlet("/hello")
public class InitFail extends HttpServlet {
    private String message = "hello, world";
    private static Logger logger = Logger.getLogger(InitFail.class.getCanonicalName());
    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("初始化");
        ServletContext context = this.getServletContext();
        int count = 0;
        Object cnt = context.getAttribute("count");
        if (cnt == null){
            count = 0;
        } else {
            count = Integer.parseInt(cnt.toString());
        }
        logger.info("count:" + count);
        if (count < 3){
            this.getServletContext().setAttribute("count", count + 1);
            throw new UnavailableException("服务不可用", 10);
        }
        message = "hello, world";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println(message);
    }
}
```
```bash
$ curl localhost:8080/servlet-hello/hello
HTTP状态 503 - 服务不可用。
当前不可用。由于临时过载或计划维护，服务器当前无法处理请求，这可能会在一些延迟后得到缓解。
```
#### 2.3.2.2 工具相关
就是说，只听 init 方法的，其余不要听。

> The triggering of static initialization methods when a tool loads and introspects a
Web application is to be distinguished from the calling of the init method.
Developers should not assume a servlet is in an active container runtime until the
init method of the Servlet interface is called. For example, a servlet should not try
to establish connections to databases or Enterprise JavaBeans™ containers when
only static (class) initialization methods have been invoked.
### 2.3.3 处理请求

当servlet实例初始化完成后，容器就可以使用它处理请求，还是那句话，请求数据封装在
ServletRequest对象中，servlet 从中读取，并把响应写入另一个预先提供的对象 ServletResponse 对象中。
这两个对象作为 service 方法的参数提供。

对于 HttpServlet，这两个对象将是 HttpServletRequest 和 HttpServletResponse。

注意，servlet 生命周期内可能不会处理任何请求。
#### 2.3.3.1 多线程问题
再次强调容器会并发调用servlet 的service 方法，开发者必须注意多线程问题。

并强烈不建议在 service 方法上使用 synchronized 关键字，因为这样极度影响效率。
#### 2.3.3.2 处理请求过程中抛出异常
servlet 可以在处理请求过程中抛出 ServletException 异常或者 UnavailableException 异常。
前者表示处理过程中遇到了错误，此时容器应当妥善清理请求。
UnavailableException 表示 servlet 暂时或者永久无法处理对应的请求。

当 UnavailableException 指示永久无法处理请求时，容器必须移除 servlet 实例，调用其
destroy 方法并释放 servlet 实例，由此导致的所有拒绝服务的请求，容器必须返回
SC_NOT_FOUND（404）响应。

当 UnavailableException 指示暂时无法处理请求时，容器可以拒绝服务期间屏蔽所有到此
servlet 实例的请求，并对这些请求返回 SC_SERVICE_UNAVAILABLE（503）响应，同时在
响应中设置 Retry-After 首部。

容器也可以不区分这两者，而一律把 UnavailableException 当作永久无法提供服务处理。
ps：但是这种一刀切的处理方式不够健壮。

ps: 根据 UnavailableException 的 API 文档，使用  UnavailableException(String msg)
表示永久拒绝服务，使用 UnavailableException(String msg, int seconds) 表示临时
拒绝服务。小于等于零的超时时间表示servlet无法预估服务不可用的时间。

```java
{{ #include code/servlet-hello/UnavailableDemo.java}}
```

#### 2.3.3.3 异步处理
有些时候，servlet 处理请求时需要等待一段时间（比如等待jdbc连接，等待另一个 web 服务响应），
才能写入响应数据，这种情形在 servlet 内部忙等对效率极为不利。


#### 2.3.3.4 线程安全
#### 2.3.3.5 升级处理
### 2.3.4 结束服务
