# servlet 编码
2021-09-29

servlet 4.0 要求，解析请求数据时：
ServletRequest.setCharacterEncoding()、ServletContext.setRequestCharacterEncoding()
以及 web.xml 的 request-character-encoding 元素可以强行设置容器解析请求数据时使用的字符集编码。

如果上述三个地方都没有设置：
- 当 Context-Type 为 application/x-www-form-urlencoded 时，
  根据 Context-Type 的设置，如果 Context-Type 也没有设置，则使用 US-ASCII 解析请求数据（包括首部和请求体）。
- 当 Context-Type 为 其它类型时，则尝试查找 web app、容器厂商设置的默认编码，如果都没有，则使用 ISO-8859-1。

servlet 规范对响应编码没有做约束。

## 一个 demo
粗略浏览 servlet spec 4.0，看到了编码相关的内容。编写一个例子测试一下

demo 功能描述

这个 servlet 的 servlet path 为 /servlet-hello，

它接收 GET 或 POST 方法发送的 applicatoin/x-www-form-urlencoded 类型的表单数据，
拼接成字符串，以纯文本方式返回，同时在控制台输出取得的请求数据。

为了测试编码，提供调整编码的方式：
发送 request-encoding=UTF-8 可以把后续解析请求的编码修改为 UTF-8，其余编码类推。
发送 response-encoding=UTF-8 可以把后续响应请求时使用的编码修改为 UTF-8，其余编码类推。
发送 request-encoding=reset 或者 response-encoding=reset 可以重置编码为默认编码。

默认，是不指定编码的。

TODO：期望输出请求、响应的二进制形式，可以观察的更深入。

### servlet 代码
<details><summary> 点击展开 </summary>

```java
{{#include code/servlet-hello/EncodingTest.java}}
```
</details>

### demo 通信测试
```bash
context_path=http://localhost:8080/servlet-hello
url=$context_path/encoding-test

curl $url -d name=Jack -d hello=world
```
返回的数据
```
name:Jack
hello:world
```
控制台输出的日志
```
Sytem Default Charset is:GBK
current request encoding is default encoding
name:Jack
hello:world
current response encoding is default encoding
```
### 不设置请求编码

```bash
context_path=http://localhost:8080/servlet-hello
url=$context_path/encoding-test

```

1. UTF-8 发送中文 `curl $url -d name=中国` 后台乱码，但响应不乱
2. UTF-8 发送中文，在 Content-Type 中指定编码
```bash
curl $url -d name=中国 -H "Content-Type: application/x-www-form-urlencoded;charset=utf-8"
```
后台不乱，响应会乱。

设置响应编码为 UTF-8. `curl $url -d response-encoding=UTF-8`
3. 再次用 UTF-8 发送中文，在 Content-Type 中指定编码
```bash
curl $url -d name=中国 -H "Content-Type: application/x-www-form-urlencoded;charset=utf-8"
```
后台和响应都不乱码。

4. GBK 发送中文，在 Content-Type 中指定编码
```bash
curl $url -d name=$(iconv -f utf-8 -t GBK <<<中国) -H "Content-Type: application/x-www-form-urlencoded;charset=GBK"
```
后台和响应都不乱码。很好。

在第 2. 步有一个小疑问，不设置响应编码的的时候，响应数据是什么编码的？
使用 file 命令测试响应数据，说是 ASCII 编码。
```bash
$ curl $url -d response-encoding=reset
# 此时，不设置响应编码
$ curl $url -d name=中国 -s | file -
/dev/stdin: ASCII text
```
### 显式配置编码，就按配置的来解析
```bash
curl $url -d name=Jack

# 切换为 GBK 解码请求
curl $url -d request-encoding=GBK

# 服务器接收乱码，返回值乱码
curl $url -d name=中国

# 接收和返回值都不乱吗
curl $url -d name=$(iconv -f UTF-8 -t GBK <<< 中国)
```
