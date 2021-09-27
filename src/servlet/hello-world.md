# hello-world
2021-09-27

- 产物：一个 java web 项目（servlet-hello），部署到 tomcat 下，
  浏览器访问 http://localhost:8080/hello/html 可以得到一个 HTML 页面，其上显示 “Hello, World”；
  命令行访问 curl http://localhost:8080/hello/text 可以得到一个文本 “Hello, World”。

- 目的：
  - 感受最简单的 servlet 程序开发的完整流程
  - 搭建 java web 基础环境
  - 演示命令行、maven、IDEA 开发 java web 项目的方式

- 使用多种方式开发部署 servlet-hello 项目
  - 命令行 + 文本编辑器：演示底层原理
    - 使用命令行创建项目
    - 使用文本编辑器编写代码
    - 使用命令行编译
    - 使用命令行部署启动
    * 最直白的展示 java web 开发的底层逻辑；但只适合极其简单的场景，如果不借助其它
      工具，开发 java web 项目会是个灾难。
  - maven  + IDEA
    - 使用 maven 创建项目
    - 使用 IDEA 编写代码
    - 开发时使用 IDEA 部署调试
    - 上线时使用 maven 打包部署

    * maven 管理依赖、打包项目很方便；maven 也能部署，但并不好用。
      使用 maven 做构建工具，可以使其独立于具体 IDE，方便合作、分享、迁移。
      这里使用文本编辑器编辑，或者使用IDE编辑代码都无所谓。
    * 使用IDE可以享受代码提示的便利，各种IDE都能适配Maven项目，这样也不会失去使用
      maven 管理项目依赖的好处。ide 还能支持快速部署调试项目。
      各种IDE中，IDEA 和 Eclipse 是比较好的。我花钱买了正版IDEA，就用IDEA吧。

## 准备 JDK 和 Tomcat
JDK 选择 JDK 8，Tomcat 选择 9。
JDK 用于编译代码，用于支撑 Tomcat 运行。
Tomcat 用于发布开发好的 web 应用。
```
export CATALINA_HOME=/mnt/d/programs/server/apache-tomcat-9.0.45
export JAVA_HOME=/opt/jdk/jdk8
export PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH

$ javac -version
javac 1.8.0_291
$ catalina.sh version
Using CATALINA_BASE:   /mnt/d/programs/server/apache-tomcat-9.0.45
Using CATALINA_HOME:   /mnt/d/programs/server/apache-tomcat-9.0.45
Using CATALINA_TMPDIR: /mnt/d/programs/server/apache-tomcat-9.0.45/temp
Using JRE_HOME:        /opt/jdk/jdk8
Using CLASSPATH:       /mnt/d/programs/server/apache-tomcat-9.0.45/bin/bootstrap.jar:/mnt/d/programs/server/apache-tomcat-9.0.45/bin/tomcat-juli.jar
Using CATALINA_OPTS:
Server version: Apache Tomcat/9.0.45
Server built:   Mar 30 2021 10:29:04 UTC
Server number:  9.0.45.0
OS Name:        Linux
OS Version:     5.10.16.3-microsoft-standard-WSL2
Architecture:   amd64
JVM Version:    1.8.0_291-b10
JVM Vendor:     Oracle Corporation
```
## 命令行+文本编辑器
1. java 是跨平台的，windows 和 Linux 都是一样的。
2. 命令行，Linux 的命令行比较好用，在 Linux 编辑。
3. 文本编辑器选择 vim。
### 创建项目结构
Java Web 项目对源代码目录结构没有要求，只对最终的 webapp 目录布局有规定。
源码目录结构，一般采用 maven 的布局风格。
为了简单，就不讲究目录结构了。
```bash
mkdir servlet-hello

```
### 编写代码
编写两个 Servlet: HelloHtmlServlet 和 HelloTextServlet

<details><summary>点击展开源码：HelloHtmlServlet.java</summary>

```java
{{#include code/servlet-hello/HelloHtmlServlet.java}}
```
</details>

<details><summary>点击展开源码：HelloTextServlet.java</summary>

```java
{{#include code/servlet-hello/HelloTextServlet.java}}
```
</details>

<details><summary>可以使用如下命令快速生成代码</summary>

```bash
cd servlet-hello
# 1. HelloHtmlServlet.java

cat <<EOF > HelloHtmlServlet.java
// 导入Java库
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HelloHtmlServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
          throws ServletException, IOException{
          // 设置相应内容类型
          response.setContentType("text/html");
          // 返回响应数据
          PrintWriter writer = response.getWriter();
          writer.println("<h1> Hello, World</h1>");
    }
}
EOF
# 2. HelloTextServlet.java
cat <<EOF > HelloTextServlet.java
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HelloTextServlet extends HttpServlet{

    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
                    throws ServletException, IOException{
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();
        out.println("Hello, World");
    }
    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
                      throws ServletException, IOException{
        this.doGet(request, response);
    }
}
EOF
```
</details>

### 编译代码
servlet API 不是 JDK 标准类库，要依靠第三方提供，使用 Tomcat 自然就由 Tomcat 提供。
编译的时候把相关 jar 包的路径添加到 classpath 中。
```bash
export CATALINA_HOME=/mnt/d/programs/server/apache-tomcat-9.0.45
export JAVA_HOME=/opt/jdk/jdk8
export PATH=$JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH

javac -cp $CATALINA_HOME/lib/servlet-api.jar HelloHtmlServlet.java
javac -cp $CATALINA_HOME/lib/servlet-api.jar HelloTextServlet.java
```

### 部署到tomcat
每个 webapp 要准备一个 web.xml 文件。

```xml
{{#include code/servlet-hello/web.xml}}
```

<details><summary>命令行</summary>

```bash
cat <<EOF > web.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                  http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
                version="4.0">
  <servlet>
    <servlet-name>hello-html</servlet-name>
    <servlet-class>HelloHtmlServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>hello-html</servlet-name>
    <url-pattern>/html</url-pattern>
  </servlet-mapping>

  <servlet>
    <servlet-name>hello-text</servlet-name>
    <servlet-class>HelloTextServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>hello-text</servlet-name>
    <url-pattern>/text</url-pattern>
  </servlet-mapping>
</web-app>
EOF
```
</details>

然后部署

```bash
mkdir -p dist/WEB-INF/classes
cp web.xml dist/WEB-INF
cp *.class dist/WEB-INF/classes

cp -r dist $CATALINA_HOME/webapps/hello
```
### 启动tomcat
```bash
catalina.sh run
```
### 访问测试
```bash
$ curl localhost:8080/hello/text
Hello, World
```
### 附：Maven风格目录结构
```bash
mkdir -p servlet-hello/src/main/{java,resources,webapp}
mkdir servlet-hello/src/main/webapp/WEB-INF
```
## Mavn + IDEA
### 创建项目
使用命令 `mvn archetype:generate` 创建 maven 项目，输出如下
```cmd
[INFO]
[INFO] --- maven-archetype-plugin:3.2.0:generate (default-cli) @ standalone-pom ---
[INFO] Generating project in Interactive mode
[WARNING] No archetype found in remote catalog. Defaulting to internal catalog
[INFO] No archetype defined. Using maven-archetype-quickstart (org.apache.maven.archetypes:maven-archetype-quickstart:1.0)
Choose archetype:
1: internal -> org.apache.maven.archetypes:maven-archetype-archetype (An archetype which contains a sample archetype.)
2: internal -> org.apache.maven.archetypes:maven-archetype-j2ee-simple (An archetype which contains a simplifed sample J2EE application.)
3: internal -> org.apache.maven.archetypes:maven-archetype-plugin (An archetype which contains a sample Maven plugin.)
4: internal -> org.apache.maven.archetypes:maven-archetype-plugin-site (An archetype which contains a sample Maven plugin site.
      This archetype can be layered upon an existing Maven plugin project.)
5: internal -> org.apache.maven.archetypes:maven-archetype-portlet (An archetype which contains a sample JSR-268 Portlet.)
6: internal -> org.apache.maven.archetypes:maven-archetype-profiles ()
7: internal -> org.apache.maven.archetypes:maven-archetype-quickstart (An archetype which contains a sample Maven project.)
8: internal -> org.apache.maven.archetypes:maven-archetype-site (An archetype which contains a sample Maven site which demonstrates
      some of the supported document types like APT, XDoc, and FML and demonstrates how
      to i18n your site. This archetype can be layered upon an existing Maven project.)
9: internal -> org.apache.maven.archetypes:maven-archetype-site-simple (An archetype which contains a sample Maven site.)
10: internal -> org.apache.maven.archetypes:maven-archetype-webapp (An archetype which contains a sample Maven Webapp project.)
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 7: 10
Define value for property 'groupId': cc.xizhan.demo
Define value for property 'artifactId': servlet-hello
Define value for property 'version' 1.0-SNAPSHOT: :
Define value for property 'package' cc.xizhan.demo: :
Confirm properties configuration:
groupId: cc.xizhan.demo
artifactId: servlet-hello
version: 1.0-SNAPSHOT
package: cc.xizhan.demo
 Y: :
```

### IDEA 打开项目
File -> Open，选择路径 F:\java\servlet\servlet-hello，其下有个 pom.xml，选择它，
open as project 搞定。
IDEA 会自动识别 Maven 项目的类型，开启对应的特性支持。
### IDEA 配置本地部署
方便开发调试。

略。

