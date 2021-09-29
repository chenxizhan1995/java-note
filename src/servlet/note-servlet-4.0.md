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

Q. 请求中如果指定了编码，容器会相应编码吗？
测试一下。
```bash
curl "localhost:8080/servlet-hello/parameter-demo" \
  -d name=中国

现象：响应数据正常；服务器日志输出乱码（A）

curl "localhost:8080/servlet-hello/parameter-demo" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=utf-8" \
  -d name=中国
现象：响应数据乱码（？？？），服务器日志是另一种乱码（B）
说明 charset 设置有效果。

curl "localhost:8080/servlet-hello/parameter-demo" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=utf-8" \
  --data-urlencode name=中国
现象：响应数据乱码（？？？），服务器日志是第二种乱码（B）
说明发送的数据是否使用url编码，无所谓。

curl "localhost:8080/servlet-hello/parameter-demo" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=GBK" \
  --data-urlencode name=中国
现象：响应数据乱码（？？？），服务器日志正常

curl "localhost:8080/servlet-hello/parameter-demo" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=GBK" \
  -d name=中国
现象：响应数据乱码（？？？），服务器日志正常

curl "localhost:8080/servlet-hello/parameter-demo" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=GBK" \
  -H "Accept-Charset: UTF-8; q=1" \
  -d name=中国
现象：响应数据乱码（？？？），服务器日志正常
说明 Accept-Charset 对 tomcat 不管用。

curl "localhost:8080/servlet-hello/parameter-demo" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=GBK" \
  -H "Accept-Charset: GBK; q=1" \
  -d name=中国
```
## 3.13 请求对象的生存周期
request 对象的生存周期仅限于 service() 执行过程中，或者某个 filter 的 doFilter
方法内。
启用异步处理过程，生命周期便于此不同了，一旦开始异步处理，request 对象的生命周期
直到 AsyncContext 的 complete 方法被调用周结束。

应用程序保留 request 对象的引用，直到生命周期结束仍然保留，是未定义的行为，不要这么干。

# 4. Servlet 上下文

# 5. 响应
# 6. 过滤
# 7. 会话
# 8. 注解和可插拔性
# 9. 请求转发
dispatching request
# 10. web 应用
# 11. 应用生命周期事件

# 12. 把请求映射到servlet
# 13. 安全性
# 14. 部署描述符
# 15. 与其它相关规范的要求
Requirements related to other Specifications
# 附. 修订记录

