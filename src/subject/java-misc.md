# 杂谈
2021-10-21

字数少于 800 的短篇文章。
# 使用OpenJDK必须开源代码吗？
2021-10-21

不用开源。

OpenJDK 的协议不是标准的GPL，而是 GPL with Classpath Exception。
通俗点说，就是你用它的模块，比如Java代码里面，常见的import语句，比如`import java.*, javax.*, javafx.* etc.`等等，
这些都属于exception里面的，也就是说，你只是import这些类库，并且使用它们的话，不受GPL协议影响，你大可以在此基础之上，制作自己的软件，并且闭源发布

你魔改了Open JDK的实现的话，比如你更改了java.base.jmod模块的源代码的话，那你的代码就会被GPL传染上，要求必需开源，否则就违背了Open JDK的开源协议,
，当初SUN这样做的目的很简单，就是为了防止出现不同版本的Java。

[OpenJDK的开源协议，闭源没问题 - 知乎](https://zhuanlan.zhihu.com/p/54094942)

# Q. Maven 支持 java 11 吗？Ans：支持

Ans：大概是支持的。Maven的 docker 镜像最新版本为：3.8.3-openjdk-17，所以11，17都支持。

# Java 更新周期
2017年9月，Java平台的主架构师 Mark Reinhold 发出提议，要求将Java的功能更新周期从之前
的每两年一个新版本缩减到每六个月一个新版本。该提议获得了通过，并在提出后不久生效。

JDK 将分为长期支持版本和快速发布版本。快速发布版本提供6个月支持，发布六个月之后即不再提供更新。
长期支持版本提供 8 年的支持。

当前（2021-10-21）JDK8,JDK11,JDK17是长期支持版本。每三年发布一个长期支持版本。
PS：自JDK9开始变得规律。

- Java SE 8 (LTS) 	2014 年 3 月
- Java SE 11 (LTS) 	2018 年 9 月
- Java SE 17 (LTS) 	2021 年 9 月
