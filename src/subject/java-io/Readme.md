# Java I/O
2021-10-04
Java IO

## 标准IO、NIO、NIO.2
Java 诞生时提供了基于流的IO系统，打包在 java.io 中。

JDK 1.4 增添了基于缓冲区和通道的IO，打包在 java.nio 及其子包中。
JDK 1.7 对 nio 做了极大增强，所以经常使用术语 NIO.2。

特别指出，NIO 不是为了替代标准IO而提供的，相反，NIO 是作为标准IO的补充而提供的。

