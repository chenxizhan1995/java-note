# 标准IO
2021-10-05

[标准IO类和接口-JDK11](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/package-tree.html)

可以看出 java.io 包含了许多类和接口，包含字符流、字节流以及对象串行化。

先讨论最特殊的 IO 类：File。

## File 类

java.io 中的类大多数都是处理输入输出流的，但 File 类不是，它用于操作文件系统本身。
它的实例是对文件系统路径的封装，它能
- 判断文件/目录是否存在
- 判断路径对应的是否为目录 isDirectory()
  - 如果目录对应的文件/目录不存在，那它怎么可能准确判断是否为目录呢？
  - Ans：不能，所以 isDirectory 返回 true 当且仅当指定路径真实存在且为目录，其它情况返回 false。
- 读取文件属性：是否可读、可写、可执行
- 列出目录下的文件/目录列表
- 创建、删除、重命名文件和目录

NIO 中的 java.nio.Path、java.nio.Paths、java.nio.Files 在许多情况下都为 java.io.File 类提供了强大的替换方案。

对文件和目录的路径的抽象表示。
> An abstract representation of file and directory pathnames.
### 构造函数
- File​(File parent, String child)
    Creates a new File instance from a parent abstract pathname and a child pathname string.
- File​(String pathname)
    Creates a new File instance by converting the given pathname string into an abstract pathname.
- File​(String parent, String child)
    Creates a new File instance from a parent pathname string and a child pathname string.
- File​(URI uri)
    Creates a new File instance by converting the given file: URI into an abstract pathname.
### java 中路径表示方法
最基础的，Linux 和 Windows 路径直接填进去，就能构造出对应的文件系统路径：
```java
File f = new File("C:\\data\\foo.txt");
File f = new File("/data/foo.txt");
```

还有一种 file:// 开头的形式

### file:// 协议
[rfc8089](https://datatracker.ietf.org/doc/html/rfc8089)
```
file-URI       = file-scheme ":" file-hier-part

file-scheme    = "file"

file-hier-part = ( "//" auth-path )
                / local-path

auth-path      = [ file-auth ] path-absolute

local-path     = path-absolute

file-auth      = "localhost"
                / host
```

人话就是，file 是这个格式：`file://example.com/path/to/file`
中间的域名为 localhost 时可以省略：
所以`file://localhost/path/file` 常常写作 `file:///path/to/file`，这就是最常见的
样子。

另外，file: 为 `//localhost`还可以把整个都省略，所以这种格式也是 `file:/path/to/file`
也是可以的。
但最常用的还是 `file:///path/to/file`的形式。其中 `/path/to/file` 是本地文件的绝对路径。

ps：和这种形式一样：`https://example.com/path/to/foo.html`

Windows 下 path-absolute 为 `/C:/path/to/file` 形式，所以常见的 windows 文件路径
对应的file格式为 `file:///c:/path/to/file`。

把定义中的 local-path 修改为
```
local-path     = [ drive-letter ] path-absolute

drive-letter   = ALPHA ":"
```

所以windows上的 `C:\path\to\file` 也可以表示为`file:C:/path/to/file`，不常见。

示例：
```
file://<file-auth> <abs-path>

file://localhost/etc/hosts
file://localhost/C:/Windows/System32/drivers/etc/hosts

file:///etc/hosts
file:///C:/Windows/System32/drivers/etc/hosts

file:<abs-path>
file:/etc/hosts
file:/C:/Windows/System32/drivers/etc/hosts
```
### 路径相关的方法
绝对路径，如果已经是绝对路径了，就直接返回
- File 	getAbsoluteFile()
- String 	getAbsolutePath()

标准路径：即是绝对路径又是唯一的，且与操作系统有关。
- File 	getCanonicalFile()
- String 	getCanonicalPath()
标准路径，先转为绝对路径（如果有必要），然后根据系统平台，转为对应的唯一表示形式，
所作的操作通常包括处理 .. 和 . 、解析符号链接、以及把盘符转为标准格式（Windows下）。
实际存在的文件和目录有唯一的标准格式，不存在的文件和目录也有唯一的表示形式。
同一个路径，不存在的时候的标准形式和创建时候的标准形式可能不一样。

获取名字和父目录
- String 	getName()
- String 	getParent()
- File 	getParentFile()

转为字符串形式
- String 	getPath()
- String 	toString()
- URI 	toURI()
- Path 	toPath()

### Demo: listRoots()
```java
import java.io.File;
import java.net.MalformedURLException;

/**
 * <p> Description: java.io.File 类的用法</p>
 */
public class FileDemo {
    public static void main(String[] args) throws MalformedURLException {
        System.out.println("列出所有根");
        for (File f : File.listRoots()){
            System.out.println(f);
        }
        System.out.println("当前目录对应的绝对路径");
        File f =new File(".");
        System.out.println("absFile:" + f.getAbsoluteFile());
        System.out.println("absPath:" + f.getAbsolutePath());
        System.out.println("当前路径对应的绝对路径的 URI 形式");
        File f2 = f.getAbsoluteFile();
        System.out.println(f2.toURI());
        System.out.println(f2.toURL());
    }
}
```
在 Windows 上执行
```
列出所有根
C:\
D:\
E:\
F:\
当前目录对应的绝对路径
F:\java\java-demo\.
F:\java\java-demo\.
当前路径对应的绝对路径的 URI 形式
file:/F:/java/java-demo/./
file:/F:/java/java-demo/./

Process finished with exit code 0

```

在Linux 上执行
```
列出所有根
/
当前目录对应的绝对路径
/home/xizhan/note/java/src/subject/java-io/code/.
/home/xizhan/note/java/src/subject/java-io/code/.
当前路径对应的绝对路径的 URI 形式
file:/home/xizhan/note/java/src/subject/java-io/code/./
file:/home/xizhan/note/java/src/subject/java-io/code/./
```
### Demo：遍历目录
String[] list()
String[] list(FilenameFileter filter)

File[] listFiles()
File[] listFiles(FilenameFilter filter)
File[] listFiles(FileFilter filter)

FilenameFileter 只有一个方法：
- boolean 	accept​(File dir, String name)
FileFilter 只有一个方法
- boolean 	accept​(File pathname)

对于大的目录，使用Files.newDirectoryStream() 系列的方法更节省资源。

## InputStream 略
## OutputStream 略
## FileInputStream
读写方法都是基类定义的那几个。

构造函数
- FileInputStream​(File file)
- FileInputStream​(String name)

使用 File 类作为参数的，可以预先执行一些测试，比如判断文件是否存在。

## FileOutputStream
## ByteArrayInputStream
构造函数有两个
- ByteArrayInputStream​(byte[] buf)
- ByteArrayInputStream​(byte[] buf, int offset, int length)
都需要字节数据来提供数据源。
close() 方法对 ByteArrayInputStream 没有效果，所有没有必要调用它，但调用了也不会报错。
```java
import java.io.*;

public class ByteArrayInputStreamDemo{
    public static void main(String[] args){
        String s = "abcdefghijklmnopqrstuvwxyz";

        byte[] b = s.getBytes();

        ByteArrayInputStream input1 = new ByteArrayInputStream(b);
        ByteArrayInputStream input2 = new ByteArrayInputStream(b, 0, 3);
    }
}
```
第二个输入流只有前三个字符。

ByteArrayInputStream 实现了 mark 和 reset 方法，如果没有调用 mark 直接调用了 reset，就会回到输入流开头。

```java
import java.io.ByteArrayInputStream;

/**
 * <p> Description: 这个程序从流读取内容并输出，然后重置输入流并再次输出，这次会把字母转为大写之后再输出。</p>
 */
public class ByteArrayInputStreamReset {
    public static void main(String[] args) {
        String tmp = "abcdefghijklmn";
        byte[] b = tmp.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(b);

        for (int i = 0; i< 2; i++){
            int bt;
            while ((bt = in.read()) != -1){
                if (i == 0){
                    System.out.write(bt);
                } else {
                    System.out.write(Character.toUpperCase((char)bt));
                }
            }
            System.out.println();
            in.reset();
        }
        System.out.flush();
    }
}
```

## ByteArrayOutputStream

close() 方法对 ByteArrayOutputStream 没有效果，所有没有必要调用它，但调用了也不会报错。

## 过滤的字节流
过滤的字节流 FilterInputStream 和 FilterOutputStream  是一些简单的封装器，用于封装底层的输入输出流。
它们分别是 InputStream 和 OutputStram 的子类。
它们的典型子类有：BufferedInputStram 和 DataInputStream

## 缓冲的流
缓冲流通过为底层流附加缓冲区来提高IO性能。
同时因为使用了缓冲区，所以跳过、标记和重置方法都可以支持了。
缓冲流的类是 BufferedInputStream 和 BufferedOutputStream。PushbackInputStream 也
具有缓冲。

输入缓冲流有两个构造函数，第一个使用默认的缓冲区大小，第二个使用指定的缓冲区大小，经典的参数是 8192.
- BufferedInputStream​(InputStream in)
- BufferedInputStream​(InputStream in, int size)

输出缓冲也有两个构造函数
- BufferedOutputStream​(OutputStream out)
- BufferedOutputStream​(OutputStream out, int size)

PushBackInputStream 可以“偷窥”输入流接下来的若干字符而不影响输入流。

## SequenceInputStream
可以将多个输入流合并为一个。当读取的时候，依次从第一个输入流读取，然后是第二个。

- SequenceInputStream​(InputStream s1, InputStream s2)
- SequenceInputStream​(Enumeration<? extends InputStream> e)
## PrintStream
是 FilterOutputStream 的子类。
经常使用的 Sytem.out 和 System.err 就是此类的实例。

可以输出到底层流
- PrintStream​(OutputStream out)
- PrintStream​(OutputStream out, boolean autoFlush)
- PrintStream​(OutputStream out, boolean autoFlush, String encoding)
- PrintStream​(OutputStream out, boolean autoFlush, Charset charset)
指定字符集，在输出字符和字符串的时候就可以使用指定字符集解码。
Q. 不指定字符集的时候呢？

可以直接输出到文件

- PrintStream​(File file)
- PrintStream​(File file, String csn)
- PrintStream​(File file, Charset charset)

- PrintStream​(String fileName)
- PrintStream​(String fileName, String csn)
- PrintStream​(String fileName, Charset charset)

这个类对所有的基本类型和String类型都提供了 print 和 println 方法，输出的是适合人类阅读的格式。
如果不是基本类型，会调用它的 toString 方法后再输出。
对于 char 数组还提供了重载的 print 和 println，直接输出字符串形式。
对于其它数组，则调用toString，输出的是一串看不懂的的东西。

TODO：对于字符和字符串，会根据字符集解码吗？试试看。
基本类型，即使解码，也是ASCII字符，都是兼容的，区分不出来，但对于汉字就不同了。
## 待续
