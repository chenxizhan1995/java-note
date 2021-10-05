# 前言
2021-09-26

系统整理 Java 领域的编程知识。

<details>
  <summary>是笔记，不是教程。针对我个人知识薄弱点记录的详细，方便随时翻看，而我
  已经掌握的知识点，一笔带过。
  </summary>
  精力有限，必须有所取舍，所以先定位目标，防止跑偏。
</details>

## 大纲
- Java 语法基础

  系统复习 Java 基础语法和最基础的类库

  - 数据类型
  - 控制流
  - 初步认识类、对象、继承
  - 集合框架（基础）
  - io（基础）
  - 泛型（基础）

  * Java 选择 JDK8 学习。JDK 11 也在看。

- Java 高级语法

  *本没有基础、高级之分，常用的、容易理解的叫基础，不常用、不好理解的叫高级*
  - 集合框架
  - io
  - 泛型
  - 反射
  - 注解
  - 并发

上面两个是学习基础，下面是实际开发相关的。

- Servlet
  * 最好先学习日志，再看 servlet，方便调试
- Tomcat + Spring MVC + MyBatis
  * Tomcat 选择 Tomcat 9 + JDK 8

- 专题知识 <span title="各种类库、常见任务的处理" class="note">Info</span>
  - 开发和调试工具
    - IDEA、Eclipse
    - Maven、Gradle
  - 日志：JDKLog、commons logging、log4j、slf4j、logback
  - JOSN 处理：fastjson、gson
  - Excel 文档
  - HTTP 请求

- 随手笔记
  - TODO: 加载资源文件和类路径
  - TODO: tomcat 中的日志

  某个具体的问题、知识点、实战案例。单独一篇文章就可以讲清楚的东西。

> 分类只是大概的分类，并非一成不变，某篇笔记可能扩充为系统性笔记。

<style type="text/css" >
    .note {
      color: red;
      cursor: help;
      display: inline-block;
    }
</style>

TODO：有时间把这些拆成多个笔记。

## 其它

本笔记使用[mdBook][]维护。

[mdBook]:https://rust-lang.github.io/mdBook
## TODO
- java 加载资源文件，classLoader.loadAsStream 等
