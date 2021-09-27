# Servlet
2021-09-26

- 标准 web app 目录布局
- servlet
- filter
- handler

- web.xml 的文档结构
  - 有几个主要元素？
  - 听说支持注解方式及配置了？

Filter用到了责任链模式，Listener用到了观察者模式，Servlet用到了模板方法模式。

软件版本：JDK 8，Tomcat 9。
Tomcat 9.0.x 对应 Servlet Spec 4.0。
## misc
- Q. 关于 HttpServlet.service() 方法的 httpRequest 和 httpResponse，使用上有什么惯例和限制？
- Q. URL 映射规则？
- Q. Filter 有什么？
- Q&A. WEB-INFO/web.xml 文件头有什么变动？
  - 使用 maven-archetype-plugin:3.2.0:generate 构件，使用模板 org.apache.maven.archetypes:maven-archetype-webapp
    生成的项目代码，其 web.xml 如下：
    ```xml
    <!DOCTYPE web-app PUBLIC
    "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
    "http://java.sun.com/dtd/web-app_2_3.dtd" >

    <web-app>
      <display-name>Archetype Created Web Application</display-name>
    </web-app>
    ```
    更早的 web.xml
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <web-app xmlns="http://java.sun.com/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
            version="3.0" metadata-complete="true">
      <!-- blablabla -->
    </web-app>
    ```

  - Ans: 网上搜索，大体前者为 web.xml 2.3 的格式；自 2.4 开始都不加 doctype 元素了。比较新的 web.xml 4.0 头信息如下：
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
                    version="4.0">
    </web-app >
    ```
## 参考
[servlet的本质是什么，它是如何工作的？ - 知乎](https://www.zhihu.com/question/21416727)
[javax.servlet.http (Java(TM) EE 8 Specification APIs)](https://javaee.github.io/javaee-spec/javadocs/javax/servlet/http/package-summary.html)
[Java EE](https://javaee.github.io/)
