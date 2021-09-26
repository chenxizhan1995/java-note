# 日志
2021-09-26

日志，是如今应用程序的一个主题。

实际开发中常见的日志框架有 JDKLog、Log4J、LogBack、SLF4J、commons logging。
其中 SLF4J 和 commons logging 是日志桥接工具，可以适配不同的日志框架，便于项目中切换。

Q. 有了JDK的日志框架，为何还要开发第三方日志框架？

- 常见日志框架有哪些？各自简单介绍
- tomcat 使用的是 commons logging 日志框架，编写的 webapps 日志如何与 tomcat 日志优雅结合？
## commons logging
Apache 软件基金会有一个名为 Apache Commons 的项目，致力于可重用 Java 组件的各个方面。
> Apache Commons is an Apache project focused on all aspects of reusable Java components.

commns logging 是 Apache Commons 中的一个项目。

## 参考
[Java日志框架那些事儿](https://www.cnblogs.com/chanshuyi/p/something_about_java_log_framework.html "陈树义 - 博客园")
[Apache Commons – Apache Commons](https://commons.apache.org/)
