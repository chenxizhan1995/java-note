# servlet 路径
2021-10-03

请求中的 URI 路径包含很多重要的组成部分，可以通过 request 对象获取
- Context Path：路径中，与 ServletContext 对应的前缀。如果是根 Context，则返回空字符串；否则返回
  以 / 开头且不以 / 结尾的字符串。
- Servlet Path：路径中除去 Context Path，可以直接对应到当前 servlet 的部分，以 /
  开头，除非请求是通过 "/*" 或者 "" 匹配到的，这时返回空字符串。
- Path Info: 路径中，除去 Context Path 和 Servlet Path 剩余的部分，如果没有则返回
  null，如果有，则返回以 / 开头的字符串。

- getRequestURL   返回请求对应的完整URL，包括协议、主机、端口、路径，不包括查询字符串。Q. 未说明是否解码
- getRequestURI   不解码，返回请求的路径部分：不考虑编解码的差异，有：requestURI = contextPath + servletPath + pathInfo
- getContextPath  不解码。以 / 开头且不以 / 结尾。若为默认app（ROOT app）则返回空字符串
- getServletPath  解码。  返回app下对应到此servlet的路径，以 / 开头。若通过 /* 模板匹配到此 servlet，则返回 ""
- getPathInfo     解码，无则为null。servlet path 之后，查询字符串之前的部分。
- getQueryString  不解码，无则为null。URL 路径中的查询字符串。
## 代码
```java
{{#include code/servlet-hello/PathDemo.java}}
```
部署在 /servlet-hello app 下面。

## 访问测试
### 基本请求
```bash
$ curl -X POST --trace-ascii - "localhost:8080/servlet-hello/path"
=> Send header, 97 bytes (0x61)
0000: POST /servlet-hello/path HTTP/1.1
0023: Host: localhost:8080
0039: User-Agent: curl/7.76.1
0052: Accept: */*
005f:
== Info: Mark bundle as not supporting multiuse

requestURI:/servlet-hello/path
requestURL:http://localhost:8080/servlet-hello/path
contextPath:/servlet-hello
servletPath:/path
pathInfo:null
queryString:null

headers:
host: localhost:8080
user-agent: curl/7.76.1
accept: */*
```
### 请求体中的表单数据会出现在 queryString 中吗？Ans: 不会
```bash
$ curl -X POST --trace-ascii - "localhost:8080/servlet-hello/path" -d foo=bar
=> Send header, 165 bytes (0xa5)
0000: POST /servlet-hello/path HTTP/1.1
0023: Host: localhost:8080
0039: User-Agent: curl/7.76.1
0052: Accept: */*
005f: Content-Length: 7
0072: Content-Type: application/x-www-form-urlencoded
00a3:
=> Send data, 7 bytes (0x7)
0000: foo=bar
== Info: Mark bundle as not supporting multiuse

requestURI:/servlet-hello/path
requestURL:http://localhost:8080/servlet-hello/path
contextPath:/servlet-hello
servletPath:/path
pathInfo:null
queryString:null

headers:
host: localhost:8080
user-agent: curl/7.76.1
accept: */*
content-length: 7
content-type: application/x-www-form-urlencoded
```
### 查询字符串
```bash
$ curl -X POST --trace-ascii - "localhost:8080/servlet-hello/path" -d foo=bar -G
=> Send header, 105 bytes (0x69)
0000: POST /servlet-hello/path?foo=bar HTTP/1.1
002b: Host: localhost:8080
0041: User-Agent: curl/7.76.1
005a: Accept: */*
0067:
== Info: Mark bundle as not supporting multiuse

requestURI:/servlet-hello/path
requestURL:http://localhost:8080/servlet-hello/path
contextPath:/servlet-hello
servletPath:/path
pathInfo:null
queryString:foo=bar

headers:
host: localhost:8080
user-agent: curl/7.76.1
accept: */*
```
### 查询字符串会解码吗？Ans: 如文档所述，不会解码
```bash
$ curl -X POST --trace-ascii - "localhost:8080/servlet-hello/path" -d foo=bar --data-urlencode name=中国 -G
=> Send header, 129 bytes (0x81)
0000: POST /servlet-hello/path?foo=bar&name=%E4%B8%AD%E5%9B%BD HTTP/1.
0040: 1
0043: Host: localhost:8080
0059: User-Agent: curl/7.76.1
0072: Accept: */*
007f:
== Info: Mark bundle as not supporting multiuse

requestURI:/servlet-hello/path
requestURL:http://localhost:8080/servlet-hello/path
contextPath:/servlet-hello
servletPath:/path
pathInfo:null
queryString:foo=bar&name=%E4%B8%AD%E5%9B%BD

headers:
host: localhost:8080
user-agent: curl/7.76.1
accept: */*
```
### URL中直接使用未编码的中文，服务端会报错
```bash
$ curl -X POST --trace-ascii - "localhost:8080/servlet-hello/path" -d foo=bar --data-urlencode name=中国 -G
在请求目标中找到无效字符。有效字符在RFC 7230和RFC 3986中定义
```
