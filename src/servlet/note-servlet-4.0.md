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
对 servlet 定义了良好的生命周期：载入、实例化、初始化、提供服务、卸载。生命周期在API中体现为
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

为此，规范引入请求异步处理机制，请求异步处理允许servlet在等待资源时将线程控制权交
还给容器。
Q. 看不大懂了，接着往下看，回头或许就能理解了。
> The asynchronous processing of requests is introduced to allow the thread to return
to the container and perform other tasks. When asynchronous processing begins on
the request, another thread or callback may either generate the response and call
complete or dispatch the request so that it may run in the context of the container
using the AsyncContext.dispatch method.

异步处理的典型过程如下：
1. 容器收到请求，转手给servlet
2. servlet 读取请求参数和/或请求体
3. servlet 发起资源请求，如在队列中等待JDBC连接
4. servlet 不生成响应而直接返回
5. 当资源可用时，处理这一事件的线程继续处理过程，通过在当前线程继续处理或者通过 AsyncContext 转发。
  ps：不大理解，附带原文。
  > After some time, the requested resource becomes available, the thread handling
  that event continues processing either in the same thread or by dispatching to a
  resource in the container using the AsyncContext

<details><summary>在异步处理过程中，有些 Java EE 的特性不可用。</summary>

> Java Enterprise Edition features such as Section 15.2.2, “Web Application
Environment” on page 15-188 and Section 15.3.1, “Propagation of Security Identity
in EJB™ Calls” on page 15-190 are available only to threads executing the initial
request or when the request is dispatched to the container via the
AsyncContext.dispatch method. Java Enterprise Edition features may be available
to other threads operating directly on the response object via the
AsyncContext.start(Runnable) method.
</details>

第八章提到的 @WebServlet 和 @WebFilter 注解有个 asyncSupported 属性，默认为 false。
当设置为 true 时，web 应用可以调用 startAsync 方法开启异步处理，调用时把 request
和 response 对象传递进去，然后 servlet 就可以返回了，这个请求会在另一个线程继续处理。
Q. 响应会逆向经历过滤器，不是正常的逻辑吗？这里为什么说“this means”，这里有什么因果关系？
> This means that the response will traverse (in reverse order) the same filters (or filter
chain) that were traversed on the way in.

直到调用 AsyncContext 的 complete 方法，响应才会提交（即返回给客户端）。
调用 startAsync 线程调用 startAsync 之后，和返回之前，此线程和被 startAsync 派发
的线程会同时持有 request 和 response 对象，这里的并发控制，需要由应用程序自行负责。

asyncSupported=true 的 servlet 可以把请求转发给 asyncSupported=false 的servlet，
转发之后，当后者的 service() 方法返回后，响应就会被提交，此时由容器负责调用
AsyncContext.complete() 方法，以确保触发 AsyncListener 的监听事件。同时，在异步请求
处理中持有资源的对象，应当监听 AsyncListener.onComplete 消息以便异步处理完成时可以
释放资源。

将请求从同步servlet转发到异步servlet是不合理的，但直到异步servlet调用 startAsync
方法是才会抛出 IllegalStateException 异常，这样就允许一个 servlet 同时以异步和同步
的方式工作。
ps：也就是说，同步servlet把请求转发给异步servlet后，后者可以不调用 startAsync，而是
正常处理请求。

TODO：接下来有两段看不懂，先跳过。2021-09-29

除了注解属性，还添加了如下方法/类。
- ServletRequest
  - public AsyncContext startAsync(ServletRequest req, ServletResponse res);
    此方法把请求置于异步模式下，并使用传入的 req 和 res 以及 getAsyncTimeout()
    返回的超时时间初始化请求的 AsyncContext 对象。传入的 req 和 res 对象必须
    和 service 方法或者过滤器的 doFilter 方法收到的 req 和 res 是同一个对象，
    或者是封装了这二者的 ServletRequestWrapper/ServletResponseWrapper 的子类实例。
    到用这个方法之后，res 便不会在 service 方法结束时提交，而是在返回的 AsyncContext
    对象的 complete() 方法调用后或者 AsyncContext 方法超时之后才会提交，超时计时器
    在 req 和 res 从容器返回之后才会开始计时。Q. 什么叫“从容器返回后”？
    AsycContext 对象用来直接向 res 对象写入数据，也可以仅仅用于通知说 res 尚未
    关闭或提交。

    在不支持异步的servlet/filter 中调用此方法是非法的；在响应已被关闭且提交之后
    调用此方法是非法的，在同一个 dispatch 中再次调用此方法也是非法的。
    ……
…… 还有各种方法，用的时候再查吧。

最后给了一张异步处理状态图。没看懂。
TODO：需要一个简单的例子，演示异步处理的用法和经典用途。
[Servlet中的异步处理](https://blog.csdn.net/JavaLixy/article/details/70162488)
这里的异步，是在别的线程中处理耗时请求，不致长时间占用接受请求的工作线程，避免影响并发量。
将工作线程的控制权交回时并没有完成对请求的响应。
这里的异步，不是说完成响应请求同时在别的线程开启后台异步任务，然后交还工作线程的控制权。
Q. 但这个异步能实现这一要求吗？
#### 2.3.3.4 线程安全
request 和 response 不是线程安全的，应用程序应当负责。

如果应用程序自行创建的线程用到了由容器管理的对象，如 request 和 response，则应用
程序必须保证只在这些对象生命周期内访问对象，不然要出错的。

#### 2.3.3.5 升级处理
HTTP/1.1 定义了通用的协议升级首部，用于客户端和服务端协商使用额外的协议。
容器支持协议升级机制。但容器并不理解升级后的协议应当如何处理，升级后的请求封装为
HttpUpgradeHandler 对象，此对象以二进制字节流的方式读取、响应升级后的协议。

……若干细节。

协议升级后，将不受 servlet 过滤器的过滤。
……
### 2.3.4 结束服务
没有规定容器对载入的servlet实例保留多长时间。容器可以选择保留若干毫秒数，可以选择
保持servlet直到容器生命周期结束，可以选择这两个极端时间之间的时间段。

当容器决定移除一个servlet实例时，需调用 destroy 方法，以便servlet释放相关资源、
持久保存某些数据。例如，容器可能在内存紧张或者即将关闭的时候释放servlet实例。

容器调用 destroy 方法之前，必须允许 service() 方法中正在运行的线程执行完成，或者
等待 servlet 设置的超时时间。

一旦容器调用了 servlet 实例的destroy方法，就不得在向此实例派发任何请求；如果确实
需要重启此servlet，必须新建此servlet的实例。

当servlet实例的destroy方法完成后，容器必须释放servlet实例，以便 JVM 回收内存。

# 3. 请求
request 对象封装了来自客户端的所有相关信息。在HTTP协议中，这些信息包含在HTTP头和
HTTP请求体中。
## 3.1 HTTP 协议参数
请求参数（request parameter）是客户端作为请求的一部分发送给servlet 的字符串。

容器从 URI 的查询字符串和请求体表单中提取参数。
参数可用的时机，本节后面会有介绍。

参数以 name-value 数据对的形式存储，name 和 value 都是字符串，同一个 name 可以
对应多个 value。request 对象提供如下方法获取参数：
- getParameter(name)
- getParameterNames()
- getParameterValues(name)
- getParameterMap()

getParameterValues 以字符串数组的形式返回给定 name 对应的所有 value。
ps：name 参数不存在时返回 null，不返回长度为0的数组。
getParameter(name) 必须返回 name 对应的数组的第一个元素。
ps：name 参数不存在时返回 null。
getParameterMap() 返回所有参数构成的键值对，键是字符串，对应name，value 是字符串数组，对应name的value。

容器会把URI查询字符串和POST请求体中的参数合并，合并时URI的数据在前，POST 中的数据在后。
比如 URI 由 a=hello，请求体中有表单 a=bye&a=ok，则参数 a 的值是一个有序元组：(hello bye ok)。

作为 GET 请求组成部分的路径参数，不在这些API中暴露，需要通过 getRequestURI() 和 getPathInfo()
方法获取。
ps：路径参数，意思在路径中包含参数`http://foo.com/book/{name}`，从  `ttp://foo.com/book/a-brief-history-of-time`
中提取书籍名称`a-brief-history-of-time`。

<details><summary> 示例代码：获取参数</summary>

```java
{{#include code/servlet-hello/ParameterDemo.java}}
```
</details>

### 3.1.1 参数可用的时机
容器把表单数据存入参数集合之前，必须满足如下条件：
1. HTTP或者 HTTPS协议
2. 是 HTTP POST 方法
3. 类型是 application/x-www-form-urlencoded
4. servlet 调用了request 对象的 getParameter() 系列的方法。

若条件不满足且post保单数据未被包含进参数集合中，则要保证servlet能从request对象
的输入流中读取表单数据。若条件已满足，则表单数据不能直接request对象的输入流取得。

测试，请求方式为 HTTP GET 时，servlet 同样能从上述API获取请求参数
```bash
curl "localhost:8080/servlet-hello/parameter-demo?foo=bar"   -v
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> GET /servlet-hello/parameter-demo?foo=bar HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.76.1
> Accept: */*
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Length: 12
< Date: Wed, 29 Sep 2021 05:29:34 GMT
<

foo:[bar]
* Connection #0 to host localhost left intact
```
但更换 Content-Type 后确实取不到数据了。
```bash
$ curl "localhost:8080/servlet-hello/parameter-demo" -F foo=bar -v
*   Trying ::1:8080...
* Connected to localhost (::1) port 8080 (#0)
> POST /servlet-hello/parameter-demo HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.76.1
> Accept: */*
> Content-Length: 141
> Content-Type: multipart/form-data; boundary=------------------------78545a6d9cadb81f
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200
< Content-Length: 2
< Date: Wed, 29 Sep 2021 05:31:30 GMT
<

* We are completely uploaded and fine
```
嗯，也不全是。
```bash
$ curl "localhost:8080/servlet-hello/parameter-demo?a=b" -d foo=bar -H "Content-Type: text/plain"

a:[b]
```
Q. 这和规范中说的不一致呀？
## 3.2 上传文件
servlet 容器允许客户端使用 multipart/form-data 方式上传文件。

当满足如下条件之一时，servlet 容器提供对 multipart/form-data 的处理：
- 处理请求的servlet使用了 @MultiPart 注解
- 处理请求的servlet，在部署描述符中配置了对应的 multipart-config 元素

如何取得请求中的 multipart/form-data 数据，取决于servlet容器是否提供对此数据的处理
- 若容器提供处理，则servlet可使用 HttpServletRequest 的如下方法取得数据
  - `public Collection<Part> getParts()`
  - public Part getPart(String name)
  每个 Part 对象提供获取该部分的首部、Content-Type的方法，以及读取数据内容的
  Part.getInputStream() 方法。
  对于 Content-Disposition 为 form-data 且不含 filename 的部分，表单的字符串值可以
  通过 HttpServletRequest 的 getParameter 和 getParameterValues 方法取得。
- 若容器不提供对 multipart/form-data 的处理，则可以通过 HttpServletRequest.getInputStream
  读取请求数据。

ps：RFC 1521，multipart 不止 mutipart/form-data 一种，其它例如 multpart/mixed,
multipart/digest, multipart/alternative, multipart/parallel,等。这些类型中，
multipart/mixed 是基本类型。

## 3.3 属性（attribute）
属性是绑定在 request 对象上的键值对，一个 name 只能对应个 value。
容器可以设置属性，以表达某些无法通过API提供的特性。servlet可设置属性，以与其它servlet
交互（通过 RequestDispatcher）。通过 ServletRequest 的下述方法操作属性：
- getAttribute
- getAttributeNames
- setAttribute

以 java. 和 javax. 开头的属性保留给本规范；以 oracle. com.oracle. sun. com.sun. 开头
的属性保留个 Oracle。建议使用逆序的域名给属性命名，就和包名一样。

## 3.4 首部（header）
可以通过 HttpServletRequest 的如下方法读取 HTTP 请求的首部信息。
- getHeader
- getHeaders
- getHeaderNames

多个首部可以有相同的name，比如 Cache-Contro，getHeader() 返回给定名称（name）对应的
第一个首部，getHeaders 则返回给定名称对应的所有首部，以 `Enumeration<String>` 的格式
返回。

首部中可能包含用字符串表示的整数或者日期，HttpServletRequest 提供了快捷方法，完成转换：
- int getIntHeader(String name)
- long getDateHeader(String name)
如果 getIntHeader 不能把首部转为int，则抛出 NumberFormatException；
如果 getDateHeader 不能把首部转为 Date，则抛出 IllegalArgumentException。

ps: getHeader 不区分 name 的大消息；如果不存在指定name的首部，返回 null。
ps: 如果不存在指定name的首部，则 getHeaders 返回空的 Enumeration 对象。
ps：不存在指定 name 的首部，则 getIntHeader 返回 -1。
ps：不存在指定name的首部，则 getDateHeader 返回 -1。
ps：getDateHeader 返回自 EPOCH 以来的毫秒数。

## 3.5 请求路径
请求中的 URI 路径包含很多重要的组成部分，可以通过 request 对象获取
- Context Path：路径中，与 ServletContext 对应的前缀。如果是根 Context，则返回空字符串；否则返回
  以 / 开头且不以 / 结尾的字符串。
- Servlet Path：路径中除去 Context Path，可以直接对应到当前 servlet 的部分，以 /
  开头，除非请求是通过 "/*" 或者 "" 匹配到的，这时返回空字符串。
- Path Info: 路径中，除去 Context Path 和 Servlet Path 剩余的部分，如果没有则返回
  null，如果有，则返回以 / 开头的字符串。

使用 HttpServletRequest 的如下方法获取上述路径：
- getContextPath
- getServletPath
- getPathInfo

忽略URL编解码的区别，总保持如下等式成立：
`requestURI = contextPath + servletPath + pathInfo`

还举了一个例子。

## 3.6 翻译路径的方法
提供了两个把路径转换为本地路径的方法
- ServletContext.getRealPath
- HttpServletRequest.getPathTranslated
前者接收字符串表示的路径，返回一个字符串，对应于文件在本地文件系统的位置。
后者接收 PahtInfo，计算对应于本地文件系统的实际路径。

如果容器无法计算具体位置，这两个方法必须返回 null。
容器无法计算时实际位置的情形包括：
- web app 是未解压的形式
- web app 是在远程系统执行的，故而无法从本地系统访问

META-INFO/resource 下的 jar 包内的资源，仅当 jar 包解压之后才可获取实际路径（getRealPath）。
> Resources inside the META-INF/resources directory of JAR file must be considered
only if the container has unpacked them from their containing JAR file when a call to
getRealPath() is made, and in this case MUST return the unpacked location.

## 3.7 非阻塞I/O
异步IO可以提升负载承受能力。
只有异步servlet和异步filter才支持异步 IO。
ServletInputStream.setReadListener 和 ServletOutputStream.setWriteListener 是相关入口。

ps：有这回事就得了，到时候再看。
ps：项目上倒是用到一个文件下载功能，这不就能用上了?
## 3.8 HTTP/2 的服务端推送
服务端推送是为了提升浏览器的感知性能。

Servlet 4.0 的容器必须支持服务端推送。

ps：用到再细看。
## 3.9 Cookie
HttpServletRequest.getCookes 方法可以读取客户端请求中附带的 cookie。
cookie 是每次请求都会携带的信息，通常带回来的只有 cookie 的name和value，其它属性
不会带回来，比如服务端设置cookie时添加的注释。

## 3.10 SSL 属性
如果请求使用的是加密协议（如HTTPS）则 ServletRequest.isSecure 必须反应此信息。
同时还得暴露出下述属性：
1.  javax.servlet.request.cipher_suite      String
2.  javax.servlet.request.key_size          Integer
3.  avax.servlet.request.ssl_session_id     String

如果客户端提供了 SSL 证书，则容器还必须把证书通过规定的接口暴露给开发人员。

## 3.11 国际化
客户端可以通过 Accept-Language 首部指定首选期望的回复语言。
ServletRequest的 getLocale 和 getLocales 方法可以获取这些信息。
如果客户端没有指定，容器必须返回容器的默认locale。

## 3.12 请求数据的编码
当前，客户端发送请求时设置的 Content-Type 大都没有指定字符集编码。如此服务端解码
客户端请求时使用的编码方案就自行决定了。

若客户端未指定请求内容的字符集编码，且请求内容为application/x-www-form-urlencoded
时，容器解析首部和请求体数据必须使用 US-ASCII 编码，任何 %nn 编码的值必须通过解码
为 ISO-8859-1 字符。

对于其它类型的 Content-Type，若客户端请求、web app 配置、容器厂商配置均未指定编码
字符集，必须使用 ISO-8859-1 作为解析请求首部和请求体时的字符集编码。为了让开发者
感知到客户端请求并未指定字符集编码， getCharacterEncoding() 方法必须返回 null。

ps：也就是，返回 null，是客户端未指定字符集同时也未配置编码字符集，但容器仍旧按照
  默认字符集解码。
ps：Tomcat 默认编码是什么？

如果客户端请求未指定编码且使用的编码和默认编码不一致，会解析出错。为了解决此问题，
指定了三处接口：
1. ServletContext 的 setRequestCharacterEncoding(String enc) 方法
2. web.xml 的 request-character-encoding 元素
3. ServletRequest 的 setCharacterEncoding(String enc) 方法

开发者可以调用这个方法指定容器解析请求数据时使用的编码，必须在读取 request 的任何
数据之前设置，否则设置的编码不会生效。

## 3.13 请求对象的生存周期
request 对象的生存周期仅限于 service() 执行过程中，或者某个 filter 的 doFilter
方法内。
启用异步处理过程，生命周期便于此不同了，一旦开始异步处理，request 对象的生命周期
直到 AsyncContext 的 complete 方法被调用周结束。

应用程序保留 request 对象的引用，直到生命周期结束仍然保留，是未定义的行为，不要这么干。

# 4. Servlet 上下文
## 4.1  接口 ServletContext 简介
ServletContext 为 servlet 提供了所在 web app 的视图。servlet 容器厂商负责提供
此接口的具体实现。servlet 可以用 ServletContext 对象输出日志、获取资源的URL路径、
读写属性（与同一个容器内的其它servlet交换数据）。

每个 ServletContext 对应于 web 服务器的一条路径。例如，某个 servlet context 可能
绑定在 http://example.com/catalog，所有以 /catalog 开头的请求都将由此 servlet
context 处理。
## 4.2 ServletContext 对象的作用域
一个 servlet 容器内的每个 web app 对应唯一一个 ServletContext 实例。对于分布式
servlet 容器，web app 在每个 JVM 实例上将有一个 ServletContext 实例。

不属于任何一个 web app 的 servlet 隐含属于默认 web app，这个默认 web app 有一个
默认 servlet context。即使在分布式部署环境下，这个默认的 servlet context 也只
可以有一个实例，只在一个 JVM 上。

> Servlets in a container that were not deployed as part of a Web application are
implicitly part of a “default” Web application and have a default ServletContext. In
a distributed container, the default ServletContext is non-distributable and must
only exist in one JVM.

Q. 如何找找这个默认 Servlet Context？
## 4.3 初始化参数
ServletContext 接口提供如下方法，供 servlet 获取 web app 的初始配置参数。
- getInitParameter
- getInitParameterNames

初始化参数的用途：为方便开发者设置启动信息。
> Initialization parameters are used by an Application Developer to convey setup
information. Typical examples are a Webmaster’s e-mail address, or the name of a
system that holds critical data.

## 4.4 配置方法
自 servlet 3.0，在 ServletContext 添加了一些方法，提供编程定义 servlet、filter、对应
的 url 模板的功能。
这些方法只能在 app 初始化过程中调用。
ServletContextListener.contexInitialized, ServletContainerInitializer.onStartup。

这些方法对编程框架很有用。
……

## 4.5 上下文属性
ServletContext 接口提供如下方法操作属性：
- setAttribute
- getAttribute
- getAttributeNames
- removeAttribute
## 4.6 资源
ServletContext 接口的
- getResource
- getResourceAsStream
一些路径规则映射规则……

这两个方法，接收以 / 开头的资源路径，然后到上下文的根路径或者 WEB-INF/lib 下的jar包内的
META-INF/resources/ 为起点寻找资源。

先到上下文根路径找，找不到才去jar包中找。jar包中的路径，还有一些规则。

## 4.7 多个主机和上下文
如果服务器支持虚拟主机特性，则每个虚拟主机要有各自的 web 上下文集合，各个虚拟主机
之间的上下文集合不可共享。

ServletContext.getVirtualServerName() 方法可以获取 ServletContext 所在的逻辑主机
的名称。同一个逻辑主机内的所有上下文对象取得的主机名必须相同，必须具备持久性，必须
是具备唯一性，必须和服务器配置有关联性。

## 4.8 重载上下文的考虑
？没看懂。
有一些和classloader 相关的东西。我不会。
### 4.8.1 临时工作目录
容器厂商必须为每个容器实例提供一个私有临时目录，通过上下文属性 javax.servlet.context.tempdir
暴露给开发者，且此属性必须是 java.io.File 类型。
servlet 上下文重启时，servlet 容器不需要确保临时目录的内容，但要确保其内容不会被
其它context访问到。
# 5. 响应
response 对象保存了所有要返回给客户端的数据。在HTTP协议下，这些数据通过 HTTP 首部
和消息体传输给客户端。
## 5.1 缓冲
servlet 容器可以不提供缓冲。但如果提供了缓冲，就要遵守如下约定。

ServletResponse 定义了如下方法供开发者获取换成信息。
- getBufferSize()   返回底层缓冲区大小，单位 byte，如果没有缓冲，则返回 int 值 0.
- setBufferSize()
- isCommited()
- reset()
- resetBuffer()
- flushBuffer()

servlet 可以通过 setBufferSize 设置缓冲区大小，容器需要提供大于等于要求的尺寸的缓冲。
在使用 respose 的 Writer 和/或 ServletOutputStream 写入任何数据之前调用
setBufferSize 方法才有效，否则抛出 IllegalStateException。

> The method must be called before any content is written using a ServletOutputStream
or Writer. If any content has been written or the response object has been
committed, this method must throw an IllegalStateException.

isCommited 方法查询是否向客户端返回了任何字节的数据。flushBuffer 用于刷新缓冲，把
数据立即发送给客户端。

reset 和 resetBuffer 的作用。

> The reset method clears data in the buffer when the response is not committed.
Headers, status codes and the state of calling getWriter or getOutputStream set
by the servlet prior to the reset call must be cleared as well. The resetBuffer
method clears content in the buffer if the response is not committed without clearing
the headers and status code.
If the response is committed and the reset or resetBuffer method is called, an
IllegalStateException must be thrown. The response and its associated buffer will
be unchanged.

一旦向客户端实际发送了数据，状态就变为 commited。
> When using a buffer, the container must immediately flush the contents of a filled
buffer to the client. If this is the first data that is sent to the client, the response is
considered to be committed.

## 5.2 首部
HttpServletResponse 提供如下方法操作响应的首部。
- setHeader()   设置首部，若已存在同名首部，会清除它们
- addHeader()   设置首部，若已存在同名首部，保留；若不存在同名首部，创建。

为了方便，提供了设置 int 和 Date 类型首部的快捷方法
- setIntHader()
- addIntHeader()
- setDateHeader()
- addDateHeader()

设置首部的时机。
要是首部（除了 trailers）生效，必须在 response 对象提交之前设置首部，响应提交之后
提交的首部被忽略。
trailer 稍特殊。
> If HTTP trailer, as specified in
RFC 7230, are to be sent in the response, they must be provided using the
setTrailerFields() method on HttpServletResponse. This method must have
been called before the last chunk in the chunked response has been written.

servlet 开发者需要负责为响应设置适当的 Context-Type 首部。禁止 servlet 容器为
Content-Type 提供默认设置，在 servlet 没有设置 Content-Type 时。
http/1.1 允许省略 Content-Type。

建议容器通过 X-Powered-By 首部提供实现信息。例如：
```
X-Powered-By: Servlet/4.0
X-Powered-By: Servlet/4.0 JSP/2.3 (GlassFish Server Open Source
Edition 5.0 Java/Oracle Corporation/1.8)
```
容器应当提供配置项，以禁用这个首部。

## 5.3 HTTP Trailer
trailer 是在 http 消息体之后发送的特殊首部。
容器提供相关操作方法。
……
## 5.4 非阻塞IO
非阻塞 IO 仅允许在异步处理中使用。
并提供了一系列相关方法。

## 5.5 快捷方法
HttpServletResponse 接口提供两个快捷方法 sendRedirect，sendError。

重定向和反馈错误信息的快捷方法。方法会设置适当的首部和消息内容。

只有响应提交前才能调用这两个方法，如果已经提交，容器必须抛出 IllegalStateException。

调用这两个方法之后，会提交并终止响应，此后写入响应的数据会被忽略。
## 5.6 国际化
servlet 需要设置响应的 locale 和 字符集。

### locale

可以调用 ServletResponse.setLocale() 方法设置 locale。可以多次调用，但只有在
响应提交之前调用才有效。

如果响应提交之前，servlet没有设置 locale，则使用容器的默认 locale 作为响应的
locale，但这种情形容器无需告知客户端使用的locale设置（如 Content-Language 首部）。

Q. 容器的默认 locale 是什么？
### 编码
```xml
<locale-encoding-mapping-list>
  <locale-encoding-mapping>
    <locale>ja</locale>
    <encoding>Shift_JIS</encoding>
  </locale-encoding-mapping>
</locale-encoding-mapping-list>
```
response-character-encoding 元素可以显式设置整个 web app 中所有响应的默认编码。
```xml
<response-character-encoding>UTF-8</response-character-encoding>
```
如果既没有配置 response-character-encoding 元素，也没有对应的 mapping，则 setLocale
使用依赖于容器的映射。调用 setCharacterEncoding, setContentType, setLocale 可以
调整响应的编码，这些方法可以多次调用，调用响应的 getWriter() 或者响应提交之后调用
上述三个方法是不会影响响应的编码的。setContentType 设置编码的前提是设置的内容类型
中指定了编码（ps：text/plain; charset=utf-8）。setLocale 方法设置编码的前提是尚未
调用过 setContentType 或 setCharacterEncoding 方法设置编码。
PS：如果调用了 setLocale、setContnetType 或者 setCharacterEncoding 之一设置了编码，
是不是就会覆盖 response-character-encoding 的设置了？

> If neither element exists or does not provide a mapping, setLocale uses a container
dependent mapping. The setCharacterEncoding, setContentType, and setLocale
methods can be called repeatedly to change the character encoding. Calls made after
the servlet response’s getWriter method has been called or after the response is
committed have no effect on the character encoding. Calls to setContentType set the
character encoding only if the given content type string provides a value for the
charset attribute. Calls to setLocale set the character encoding only if neither
setCharacterEncoding nor setContentType has set the character encoding before.

在响应提交后或者调用 getWriter() 方法之后设置编码，无效。

如果没有有效的编码设置，会使用 IOS-8859-1。

只要协议允许，容器就必须把响应使用的locale和编码告知客户端。在HTTP协议中，使用
Content-Language 首部告知locale，使用Content-Type告知编码，但如果servlet没有
设置响应的 content-type，就无法将编码告知客户端。
> Containers must communicate the locale and the character encoding used for the
servlet response’s writer to the client if the protocol in use provides a way for doing
so. In the case of HTTP, the locale is communicated via the Content-Language
header, the character encoding as part of the Content-Type header for text media
types. Note that the character encoding cannot be communicated via HTTP headers
if the servlet does not specify a content type; however, it is still used to
encode text written via the servlet response’s writer.

## 5.7 response 对象的关闭
当响应对象关闭之后，容器必须立即将响应中缓存的数据刷新到客户端。
当如下事件之一满足时，认为 servlet 已完成对请求的处理且响应对象可以被关闭:
- servlet 的 service 方法退出
- setContentLength 和 setConentLengthLong 设置了大于零的数值，同时已经向响应
  对象写入了不小于这个数值的数据
- 调用了 sendError
- 调用了 sendRedirect
- 在异步处理中调用了 complete

## 5.8 response 对象的生命周期
每个response对象仅在 servlet.service() 方法或者 filter 的 doFilter 方法内有效，
除非是异步处理过程，异步处理，则直到 AysncContext.complete() 方法之后失效。
> Each response object is valid only within the scope of a servlet’s service method, or
within the scope of a filter’s doFilter method, unless the associated request object
has asynchronous processing enabled for the component. If asynchronous processing
on the associated request is started, then the response object remains valid until
complete method on AsyncContext is called. Containers commonly recycle response
objects in order to avoid the performance overhead of response object creation. The
developer must be aware that maintaining references to response objects for which
startAsync on the corresponding request has not been called, outside the scope
described above may lead to non-deterministic behavior.

# 6. 过滤
过滤器是一种 Java 组件，可以在请求到资源和资源到客户端的时候对首部和消息体做处理。

servlet 提供了轻量级过滤器框架。它描述了如何配置过滤器，以及过滤器的使用约定和
实现语义。

过滤器的API可从在线文档查阅，配置过滤器的语法在本文档的“部署描述符”章节介绍。
## 6.1 过滤器是什么？
……

过滤器可以过滤动态和静态资源，本章用 web resources 指代这两者。

...
### 6.1.1 过滤组件示例
- ■ Authentication filters
- ■ Logging and auditing filters
- ■ Image conversion filters
- ■ Data compression filters
- ■ Encryption filters
- ■ Tokenizing filters
- ■ Filters that trigger resource access events
- ■ XSL/T filters that transform XML content
- ■ MIME-type chain filters
- ■ Caching filters
## 6.2 主要概念
本节描述过滤器的主要概念。

开发者通过实现 javax.servlet.Filter 接口创建过滤器，过滤器要有一个无参构造函数。

在 web.xml 中用 filter 元素声明过滤器。用 filter-mapping 将过滤器映射到指定的
servlet（使用servlet的逻辑名称）或者一组servlet/资源（通过url模式），过滤器
就会在需要的时候调用。

### 6.2.1 过滤器的生命周期
web app 启动后，提供服务之前，必须为每个filter声明找到对应的类，创建实例，调用其
init(FilterConfig config) 方法，初始化时filter可以抛出异常，表示无法提供服务，
如果异常是 UnavailableException，容器可选择在一段时间之后重新创建过滤器，如果不是
永久不可用。

部署描述符中每个过滤器声明，在每个容器的每个 JVM 中有且仅有一个 filter 实例。

当容器收到请求，先调用过滤器列表中的第一个过滤器的 doFilter 方法，传入
ServletRequest、ServletResponse、FilterChain 三个对象，
过滤器的 doFilter() 方法一般可以实现下述过程的全部或一部分。
1. 检测请求首部
2. 对 request 封装
3. 对 response 封装
4. 调用过滤器链中的下一个实体。如果当前就是最后一个节点，则下一个实体就是目标资源。
   通过调用 FilterChain.doFilter() 方法调用下一个实体，调用时传入 request和response对象。

   容器负责提供 FilterChain 的实现对象，实现的 FilterChain 的 doFilter 方法必须
   定位链中的下一个实体并调用它的 doFilter 方法，同时传入合适的参数。
   ...
   service 方法必须和链上的所有过滤器都必须在同一个线程中执行。
5. 调用完下一个实体之后，过滤器可以对响应进行处理
6. 或者，过滤器也可以抛出异常，表示处理出错。UnavailableException。
   出错后，容器不得尝试沿链继续执行。容器可以稍后重新触发整个过滤器链，如果异常
   不是持久异常。
7. 链中最后一个过滤器执行后，下一个节点就是目标servlet或者目标资源。
8. 容器卸载过滤器时，需先调用过滤器的 doFilter 方法。

---

1. 在一个 Web 应用程序中可以注册多个 Filter 程序，每个 Filter 程序都可以对一个或一组
Servlet 程序进行拦截。如果有多个 Filter 程序都可以对某个 Servlet 程序的访问过程进行拦截，
当针对该 Servlet 的访问请求到达时，Web 容器将把这多个 Filter 程序组合成一个 Filter 链（也叫过滤器链）。
2. Filter 链中的各个 Filter 的拦截顺序与它们在 web.xml 文件中的映射顺序一致，上一个
  Filter.doFilter 方法中调用 FilterChain.doFilter 方法将激活下一个 Filter的doFilter 方法，
  最后一个 Filter.doFilter 方法中调用的 FilterChain.doFilter 方法将激活目标 Servlet的service 方法。
3. 只要 Filter 链中任意一个 Filter 没有调用 FilterChain.doFilter 方法，目标 Servlet 的 service 方法都不会被执行。

### 6.2.2 包装请求和响应
过滤器可以封装请求和响应，如此实现强大的过滤功能。
容器不得更改对象，下一个节点收到的 request/response 对象必须和前一个节点传入的对象是同一个对象。
……
### 6.2.3 过滤器环境
部署描述符的 init-params 元素可以为过滤器设置初始参数，通过 FilterConfig 接口的
getInitParameter 和 getInitParamerNames 方法可以获取这些参数。FilterConfig 同时
提供对 ServletContext 的访问。
> A Filter and the target servlet or resource at the end
of the filter chain must execute in the same invocation thread.

### 6.2.4 配置过滤器
定义过滤器的方式有两种：@WebFilter 注解和部署描述符中的 filter 元素，开发者在此元素中
指定 filter-name、filter-class、init-params。还可以为工具操作配置其它信息（图标、
文档描述）。
容器要确保为每个过滤器声明创建一个过滤器实例。所以如果对同一个过滤器类定义两个
过滤器声明，就会有两个实例。
```xml
<filter>
  <filter-name>Image Filter</filter-name>
  <filter-class>com.example.ImageServlet</filter-class>
</filter>
```

在声明过滤器之后，汇编器使用 filter-mapping 确定每个web资源对应的过滤器。
可以使用 servlet-name 把过滤器映射到指定的 servlet，可以用 url-pattern 把过滤器
映射到指定的资源集合。
```xml
<filter-mapping>
  <filter-name>Image Filter</filter-name>
  <servlet-name>ImageServlet</servlet-name>
</filter-mapping>
```
```xml
<filter-mapping>
  <filter-name>Logging Filter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
```
Q. 同一个过滤器可以对应多个 filter-mapping 吗？

url 模板映射规则在后面有说明。
对于到给定 uri 请求，过滤器在链中的顺序，规则如下：
1. 先找到匹配的 url-pattern 对应的过滤器，按在部署描述符中定义的顺序，
2. 再使用 servlet-name 对应的过滤器，按在部署描述符中定义的顺序，

如果一个 servlet-mapping 同时包含 servlet-name 和 url-pattern 子元素，则容器必须
把它展开为多个 servlet-mapping 元素，保持顺序不变。
> If a filter mapping contains both `<servlet-name>` and `<url-pattern>`, the container
must expand the filter mapping into multiple filter mappings (one for each
`<servlet-name>` and `<url-pattern>`), preserving the order of the `<servlet-name>`
and `<url-pattern>` elements.
例如，
```xml
<filter-mapping>
  <filter-name>Multiple Mappings Filter</filter-name>
  <url-pattern>/foo/*</url-pattern>
  <servlet-name>Servlet1</servlet-name>
  <servlet-name>Servlet2</servlet-name>
  <url-pattern>/bar/*</url-pattern>
</filter-mapping>
```
等价于
```xml
<filter-mapping>
  <filter-name>Multipe Mappings Filter</filter-name>
  <url-pattern>/foo/*</url-pattern>
</filter-mapping>
<filter-mapping>
  <filter-name>Multipe Mappings Filter</filter-name>
  <servlet-name>Servlet1</servlet-name>
</filter-mapping>
<filter-mapping>
  <filter-name>Multipe Mappings Filter</filter-name>
  <servlet-name>Servlet2</servlet-name>
</filter-mapping>
<filter-mapping>
  <filter-name>Multipe Mappings Filter</filter-name>
  <url-pattern>/bar/*</url-pattern>
</filter-mapping>
```

预期高性能 web app 要缓存过滤器链，避免重复计算。

### 6.2.5 过滤器和请求转发
servlet 2.4 的新特性，可以配置过滤器在 dispatcher 的 forward 和 include 调用中生效。

通过 servlet-mapping 元素中的 dispatch 元素指定生效范围
1. REQUEST：仅过滤直接来气客户端的请求
2. FORWARE：仅过滤 dispather 的 forward 转发到的资源请求
3. INCLUDE：仅过滤 dispather 的 include 转发到的资源请求
4. ERROR：仅过滤请求出错，错误页面处理机制时的资源请求
5. ASYNC：仅过滤异步处理过程中的转发请求
6. 上述1，2，3，4，5的任意组合。
```xml
<filter-mapping>
  <filter-name>Logging Filter</filter-name>
  <url-pattern>/products/*</url-pattern>
  <dispatcher>FORWARD</dispatcher>
  <dispatcher>REQUEST</dispatcher>
</filter-mapping>
```
# 7. 会话
http 是无状态协议，会话跟踪又是 web app 的重要功能。有多种会话管理机制，但用起来都
比较复杂。为此定义 HttpSession 接口，封装底层细节。

## 7.1 会话跟踪机制
### 7.1.1 基于 cookie
这是最广泛使用的会话跟踪技术。

servlet 容器必须支持基于 cookie 的会话技术。
容器跟踪会话，使用的 cookie 的名字必须是 JSESSIONID，容器可以允许开发者配置 cookie
的名字。

容器必须提供配置会话跟踪 cookie 为 HttpOnly 的选项。
### 7.1.2 SSL 会话
HTTPS 构建在 TSL 之上，TSL 自身有会话相关信息，基于此可以轻易构建会话。
### 7.1.3 URL 重写
这是使用最少的会话跟踪技术。
当客户端不接受cookie 时，此技术可作为替补。会话id必须以 jsessionid 作为参数名。
```
http://www.example.com/catalog/index.html;jsessionid=1234
```
ps：url中分号是 param

当cookie和ssl会话技术可以用的时候，不得使用URL重写技术。
### 7.1.4 会话集成
对于不支持cookie的 HTTP 客户端容器必须提供会话支持，为了满足这一要求，容器通常会
支持基于URL重写的会话技术。
## 7.2 创建会话
容器要支持会话，需要客户端配合，若客户端不支持、不愿支持会话，容器是无法后一个请求
和前一个请求联系到一起的。开发者必须考虑到这种情况，开发的app必须能处理这种情形。

javax.servlet.http.HttpSession.getId() 可以获取会话id，
javax.servlet.http.HttpServletRequest.changeSessionId() 可以修改会话id。
## 7.3 会话作用域
HttpSession 对象的作用域必须是 web app 级别（也就是 servlet context 级别）的。
多个 servlet context 的会话id可以相同，但其它信息不得共享（比如属性设置、对象引用）。

## 7.4 在会话中绑定属性
可以在 HttpSession 上绑定属性。

监听会话属性。通过 HttpSessionBindingListener 接口的 valueBound 和 valueUnbound 方法。
方法 valueBound 必须在 HttpSession.getAttribute 方法可以读取到属性之前调用；
valueUnbound 必须在 HttpSession.getAttribute 方法无法再获取到属性之后调用。
## 7.5 会话超时
HTTP 协议没有定义会话终止信息，所以服务端无法得知客户端何时下线，所以唯一的终止
会话途径是超时。

会话的默认超时时间由容器定义，且可通过 ServletContext.getSessionTimeOut 方法或者
HttpSession.getMaxInactiveTnterval 获取，前者以分钟为单位，后者是秒。
对应有 setXxx 方法用于设置会话超时时间。

当会话到达超时时间后，必须等待此会话所有的 servlet 方法执行完成后才能删除会话。

## 7.6 最后访问时间

> The getLastAccessedTime method of the HttpSession interface allows a servlet to
determine the last time the session was accessed before the current request. The
session is considered to be accessed when a request that is part of the session is first
handled by the servlet container.

## 7.7 会话的重要语义
### 7.7.1 多线程
容器负责确保多个线程读写session属性时的线程安全问题，开发则负责属性对象本身的
线程安全问题。

必须假定从 request 或 response 获取的对象不是线程安全的，包括但不限于：
ServletResponse.getWriter() 获得的 PrintWriter 对象，
ServletResponse.getOutputStream() 活得的 OutputStream 对象。

### 7.7.2 分布式环境
web app 可以是分布式的。

……过
### 7.7.3 客户端语义

> Due to the fact that cookies or SSL certificates are typically controlled by the Web
browser process and are not associated with any particular window of the browser,
requests from all windows of a client application to a servlet container might be part
of the same session. For maximum portability, the Developer should always assume
that all windows of a client are participating in the same session.

# 8. 注解和可插拔性
# 9. 请求转发 *
dispatching request
# 10. web 应用 *
# 11. 应用生命周期事件

# 12. 把请求映射到servlet *
# 13. 安全性
# 14. 部署描述符 *
# 15. 与其它相关规范的要求
Requirements related to other Specifications
# 附. 修订记录

