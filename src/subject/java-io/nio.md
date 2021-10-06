# NIO
2021-10-05

JDK 1.7 对 NIO 做了极大的增强，常常使用术语 NIO.2。这里直接介绍NIO.2。

## java.nio.file包的Files、Paths、Path
不准确的讲， java.io.File 的功能在NIO中拆分成了两个 Path 代表路径，执行路径操作（不涉及实际
的文件系统访问），Files 则测试文件属性、删除、创建、移动文件和目录，遍历目录。
而 Paths 用于获取 Path 的实例，只有这么一个重载方法 Paths.getPath(String path, String...parts)，
自 JDK 11，Paths.getPath() 标记为过时，并代之以 Path.of() 方法，二者参数一样。
## NIO的基础概念
标准IO建立在流的概念之上。NIO则建立在缓冲区和通道的概念上。
缓冲区用于容纳数据，通道则是对数据源的抽象。

缓冲区用 java.nio.Buffer 类表示，所有的缓冲区都是它的子类。
通道则由 java.nio.channels 包定义，通道实现了 Channel 接口，Channel 接口扩展了 Closeable 接口。

- Buffer 类的常用方法:略
- 几种常见的Buffer子类：略

- 获取 Channel 的方法
  - 支持通道的对象的 getChannel 方法，如 FileInputStream.getChannel
  - Files 类的静态方法，如 newByteChannel

- 使用NIO的三类方式
  - 为基于通道的IO使用NIO：这是NIO最开始的用法
  - 为基于流的IO使用NIO：这是JDK7增强之后支持的用法
  - 为路径和文件系统操作使用NIO：也是JDK7？

### 缓冲区的基本概念：容量、界限、当前位置

A container for data of a specific primitive type.

A buffer is a linear, finite sequence of elements of a specific primitive type. Aside from its content, the essential properties of a buffer are its capacity, limit, and position:
- A buffer's capacity is the number of elements it contains. The capacity of a buffer is never negative and never changes.
- A buffer's limit is the index of the first element that should not be read or written. A buffer's limit is never negative and is never greater than its capacity.
- A buffer's position is the index of the next element to be read or written. A buffer's position is never negative and is never greater than its limit.

`0 <= mark <= position <= limit <= capacity `

缓冲区不是线程安全的。

三种不易理解的操作：
- clear() makes a buffer ready for a new sequence of channel-read or relative put operations: It sets the limit to the capacity and the position to zero.
- flip() makes a buffer ready for a new sequence of channel-write or relative get operations: It sets the limit to the current position and then sets the position to zero.
- rewind() makes a buffer ready for re-reading the data that it already contains: It leaves the limit unchanged and sets the position to zero.
## 使用NIO读取文件
这是第一种使用方式：为基于通道的IO使用NIO。

NIO 的重要应用是通过通道和缓冲区访问文件。
使用通道读取文件内容有多种方式，最常用的可能是手动分配缓冲区，然后执行显式的读取
操作把文件内容加载到缓冲区，最后操作缓冲区内的数据。
ps：另一种方式是将文件映射到缓冲区。

- NIO中要打开文件，必须使用Path对象
- 通道都得关闭使用完毕后都要关闭，可以使用try-with-resource语法简化这一操作

```java
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * <p> Description: 使用NIO读取文件内容</p>
 */
public class NIOShowFile {
    public static void main(String[] args) {
        // 打开通道
        // 分配缓冲区
        // 从通道读取数据到缓冲区
        // 输出缓冲区内容
        try(SeekableByteChannel channel = Files.newByteChannel(Paths.get("test.txt"))){
            ByteBuffer buf = ByteBuffer.allocate(128);
            for (int count = channel.read(buf); count != -1; count = channel.read(buf)){
                buf.rewind();
                while (count-- >0){
                    System.out.write(buf.get());
                }
            }
        } catch(InvalidPathException e){
            System.out.println(e);
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
```
执行结果：
```
hello
world
岁月静好不曾惜，繁华落尽终是悔
```

对这个程序做几点说明。

打开通道时，Files 文件提供了静态方法 newByteChannel，返回值是 SeekableByteChannel，如果打开的是本地文件系统的文件，则实际
返回的是 FileChannel 对象，该对象实现了 SeekableByteChannel 接口。
既然是字节通道，就使用用字节缓冲区，获取字节缓冲区使用 ByteChannel 的静态方法 allocate。

注意，把数据写入缓冲区后，读取缓冲区之间要调用一次rewind()方法，这是必须的，因为
在调用 read 方法之后，缓冲区的当前位置位于末尾，为了通过 get 读取缓冲区的内容必须通过
rewind 将当前位置重置到缓冲区的开头。
