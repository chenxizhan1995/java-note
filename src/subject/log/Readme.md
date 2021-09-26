# 日志
2021-09-26

日志，是如今应用程序的一个主题。

实际开发中常见的日志框架有 JDKLog、Log4J、LogBack、SLF4J、commons logging。
其中 SLF4J 和 commons logging 是日志桥接工具，可以适配不同的日志框架，便于项目中切换。

Q. 有了JDK的日志框架，为何还要开发第三方日志框架？

- 常见日志框架有哪些？各自简单介绍
- tomcat 使用的是 commons logging 日志框架，编写的 webapps 日志如何与 tomcat 日志优雅结合？
## 简介
### commons logging
Apache 软件基金会有一个名为 Apache Commons 的项目，致力于可重用 Java 组件的各个方面。
> Apache Commons is an Apache project focused on all aspects of reusable Java components.

commns logging 是 Apache Commons 中的一个项目。
### Log4j
Log4j 一个基于 Java 的日志记录工具，由Ceki Gülcü首创，现在是 Apache 基金会的一个项目。

ps: 公司data-governance 项目用的是 log4j 1.2.17。
### logback
Gülcü此后开创了SLF4J和Logback 项目，意图成为log4j的继任者。

Logback 算是 JAVA 里一个老牌的日志框架，从 06 年开始第一个版本，迭代至今也十几年了。
不过 logback 最近一个稳定版本还停留在 2017 年，好几年都没有更新；logback 的兄弟
slf4j 最近一个稳定版也是 2017 年，有点凉凉的意思。

### log4j2
Apache Log4j 2是Log4j 1的继任者，2014年7月其GA版本（正式发布版）发布。
该框架被从头重写，并从现有的日志解决方案中获得灵感（包括Log4j 1和JUL）。

Log4j 2的最被认可的特点之一是“异步记录器”的性能。Log4j 2利用了LMAX Disruptor 。
例如，在相同的环境下，Log4j 2可以写每秒超过18,000,000条信息，而其他框架
（像Logback和Log4j 1）每秒只能写< 2,000,000条消息。

Log4j 2提供对SLF4J、Commons Logging、Apache Flume和Log4j 1的支持。

目前来看，Log4j2 就是王者，其他日志框架都不是对手
### SLF4J

## 参考
[Java日志框架那些事儿](https://www.cnblogs.com/chanshuyi/p/something_about_java_log_framework.html "陈树义 - 博客园")
[Apache Commons – Apache Commons](https://commons.apache.org/)
[log4j - 维基百科，自由的百科全书](https://zh.wikipedia.org/wiki/Log4j)
[Java日志框架：slf4j作用及其实现原理](https://www.cnblogs.com/xrq730/p/8619156.html)
