# 使用IO
2021-10-04

- 字节流和字符流
- 标准IO和NIO的关系
- 流的顶层类
- 预定义流：System.out/in/err
- 使用字节流
- 使用字符流

## 基础概念
### 字节流和字符流
Java 中把输入输出抽象为流（Stream）。并把流分为字节流和字符流。
最早只有字节后，为了国际化以及方便处理字符，很快又提供了字符流。字符流也是
建立在字节流的基础上的。

流，是生成和使用信息的逻辑实体。？？什么意思？
### 基础接口
通过 java.io 包提供与流相关的API。

PS：JDK 1.4 引入了 NIO，提供基于管道的IO方法，位于 java.nio 及其子包中。NIO 不是为了
替代 java.io 包中的API而提供的，NIO 设计用于标准IO体系的补充。

这篇笔记介绍标准IO的基础概念和用法。

字节流的顶层类是 InputStream 和 OutputStream，字符流的顶层类是 Reader 和 Writer。
### 类和接口的层次结构
### 预定义流
java.lang.System 类封装了运行时环境的几个要素，其中包含三个预定义变量 out, err, in。
这三个字段都是 public static final 的。
System.out 是标准输出流，默认情况下是控制台，它是 PrintStream 的实例；
System.in  是标准输入流，默认情况下是键盘，它是 InputStream 的实例；
System.err 是标准错误输出，默认也指向控制台，它也是 PrintStream 的实例。

```java
/**
 * <p> Description: 三个预定义流 System.in/out/err</p>
 */
public class PredefinedIO {
    public static void main(String[] args) {
        System.out.println(System.in.getClass().getCanonicalName());
        System.out.println(System.out.getClass().getCanonicalName());
        System.out.println(System.err.getClass().getCanonicalName());
    }
}

/**
 * java.io.BufferedInputStream
 * java.io.PrintStream
 * java.io.PrintStream
 */

```
ps：PrintStream extends FilterOutputStream extends InputStream。
## 使用字节流
### InputStream 和 OutputStream 类定义的方法

InputStream 类的方法

- int available()   返回当前可读取的字节数
- void close()      关闭输入源
- void mark(int numBytes) 在输入流的当前位置做标记，此标记在继续读取 numBytes 个字节之前有效
- boolean markSupported() 如果调用流支持 mark()/reset() 则返回 true
- static InputStream nullInputStream()    JDK 11，返回一个打开的空流
- `int read()`      读取下一个字节，返回 -1 表示到达流末尾
- `int read(byte[] buffer)`                         读取 buffer.length 个字节，并返回实际读取到的字节数。返回 -1 表示到达流的末尾
- `int read(byte[] buffer, int offset, int size)`   读取 size 个字节到 buffer 中以 buffer[offsed] 开始的位置，返回实际读取的
                                                  字节数，返回 -1 表示到达流的末尾
- byte[] readAllBytes()     读取流中的所有内容并返回。如果已经到达流的末尾，则返回空数组（不是null）。JDK 9
- byte[] readNBytes(int size)   同 readAllBytes，只不过限制最多读取 size 个字节
- int    readNBytes(byte[] buf, int offset, int size)
- void reset()  将输入指针重置到之前使用 mark() 设置的标记处
- long skip(long numBytes)  跳过接下来的 numBytes 个字节
- long transferTo(OutputStream out) 把流中的数据复制到输出流，返回成功复制的字节数。since JDK 9

OutputStream 类的方法

- void close()    关闭输出流
- void flush()    立即将已缓冲的任何输出发送到其目标，即刷新缓冲区
- static OutputStream nullOutputStream()    返回一个打开的输出流，向它写入的任何数据都被忽略。（类似于 `/dev/null`），JDK 11
- void write(int b) 写入单个字节，形参是 int，但只写入低8位
- void write(byte[] buf)  向输出流写入一个完整的字节数组
- void write(byte[] buf, int offset, int numBytes)  写入指定的字节

### 文件IO
在Java中，所有到文件都可以看做由字节构成，为创建一个链接到文件的字节流，需要使用
FileInputStream 或者 FileOutputStream 类。这两个类的构造器都直接接受文件路径作为
参数。

### Demo: ShowFile 输出文件内容到标准输出

#### 最终版

```java
{{#include ../code/basic/ch10-io/ShowFile.java}}
```

#### V1 版

<details><summary>不使用try-with-resource</summary>

```java
{{#include ../code/basic/ch10-io/ShowFileV1.java}}
```

</details>

#### V2版：在 finally 中关闭文件
上述代码在文件末尾使用单独的 try 语句关闭了输入流，这种方式虽然在某些情况下可以用，
但是把 close 语句放在 finally 语句块中在一般情况下都是更好的方法。

<details><summary></summary>

```java
{{#include ../code/basic/ch10-io/ShowFileV2.java}}
```

</details>

如果读取文件的代码由于 IOException 之外的异常（比如 IOError）导致失败，finally 代码块
仍会执行并关闭文件，在这个小程序中这不是问题，但在大型程序中会导致内存泄漏。
#### v3版：在一个 try 语句中打开读取文件
大多数时候，将打开文件和读取文件的语句合并到一个 try 语句块中更加简单。\
<details><summary></summary>

```java
{{#include ../code/basic/ch10-io/ShowFileV3.java}}
```

</details>

在这种方式中，要注意把 fin 初始化为 null。

FileNotFountException 是 IOException 的直接子类，所以可以用一个条 catch 语句同时
捕获这两个异常，但如果想单独处理打开文件时发生的错误，那就不能合并。
#### 读取数据的循环
读取数据的 while 循环，这几种写法是等价的：
```java
// 写法 1：
int i;
while (-1 != (i=fin.read())){
    System.out.write(i);
}


// 写法 2：
int i;
do {
  i = fin.read();
  if (i != -1) System.out.write(i);
} while (i != -1);

// 写法 3：
int i = fin.read();
while (i != -1){
    System.out.write(i);
    i = fin.read();
}

// 写法 4：
// ps：抖个机灵
for (int i = fin.read(); i!= -1; i = fin.read()){
  System.out.write(i);
}
```
#### try-with-resource
即使如此，手动关闭文件处理起来也比较麻烦，还存在遗忘的可能。JDK 7 引入了
try-witch-resource 语句，可以自动关闭打开的资源。

1. 必须是实现了 java.lang.AutoCloseable 接口的类才行自动关闭
2. 资源变量必须在 try 中声明，不能先声明后在 try 中赋值。JDK 9 开始，可以了，但必须是 effective final 的（即只能在 try 中赋值一次）
3. 同时打开多个资源，可以用分号分隔多个声明语句
4. 打开的资源隐式为 final 的，故不可重新赋值

```java
try (InputStream fin = new FileInputStream(filePath)){
    byte[] buf = new byte[8192];
    int count = 0;
    while (-1 != (count = fin.read(buf))){
        System.out.write(buf, 0, count);
    }
} catch (FileNotFoundException e){
    System.out.println("找不到文件: " + filePath);
} catch (IOException e){
    System.err.println("发生了 IO 错误" + e);
}
```
### demo: CopyFile 复制文件
```java
{{#include ../code/basic/ch10-io/CopyFile.java}}
```
## 使用字符流
### Reader 和 Writer 定义的方法
Reader
- void close()    关了它
- void mark(int numChars)  打个标记
- boolean markSupperted()  是否支持 mark/reset
- Static Reader nullReader()  空的字符流。JDK 11
- `int raed()`      读入一个字符，返回 -1 表示到达字节流末尾
- `int read(char[] buf)`  尝试读取 buf.length 个字符，返回实际读取的字符数，返回 -1 表示已经到达字节流末尾
- `abstract int read(char[] buf, int offset, int size)`  尝试读入 size 个字符到 buf 中 以 buf[offset] 开始的位置，返回实际读取的
                  字符数，返回 -1 表示一到达字节流末尾
- boolean ready()   如果下个输入请求能够不等待就返回 true，否则返回 false
- void reset()    将输入指针重置到前面设定的标记
- long skip(long numChars) 跳过接下来的 numChars 个字符，返回实际跳过的字符数
- long transferTo(Writer writer)  将输入流的内容复制到输出流，返回实际复制的字符数。JDK 10 新增

Writer

- Writer append(char ch)
- Writer append(Charsequence chars)
- Writer append(Charsequence chars, int begin, int end)
ps：这三个是 fluent 风格的 api
PS: Charsequence 是一个接口，其子类有 CharBuffer, Segment, String, StringBuffer, StringBuilder

- void close()
- void flush()
- static Writer nullWriter()  返回一个输出字符流，它忽略任何写入的数据
- void write(int ch)      写入单个字符，注意参数是 int 类型，但只写入低16位。无需强制转换为 char
- void write(char buffer[])
- void write(char[] buf, int offset, int size)
- void write(String str)
- void write(String str, int offset, int size)
PS: 字符流和字节流的输出流的 write 方法返回值都是 void 类型。

### Demo：文件复制
```java
{{#include ../code/basic/ch10-io/CopyTxt.java}}
```

Q. 字符流对文件中的换行符有特殊处理吗？
