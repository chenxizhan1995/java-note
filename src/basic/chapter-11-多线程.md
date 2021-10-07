# 多线程程序设计 待续
2021-10-06

- 多线程的概念
- 多线程的优势
- java 对多线程的支持：内置的
- 创建线程的两种方法（Demo x 2）
- 一种创建线程的常用模式：工程方法（Demo）
- 创建多个线程：可以创建任意多的线程
- 等待线程结束
- 线程优先级

- 同步
- 同步方法
- 同步语句
- 使用 wait、notify、notifyAll 的线程通信
  - Demo：tick tock 示例
  - 注意 wait 方法的伪唤醒
- 线程的挂起和终止
  - Java 2 开始过时挂起和终止方法，替代方案是自己实现
  - Demo：线程的挂起和终止

==========
- 完整的并发知识
  - 基础定义
  - 有哪些并发问题
  - 如何解决
  - 对应到Java 和 POSIX 中，如何编程处理？

- 信号量、资源池

============
- 协程？
## 多线程的概念
进程是执行中的程序，一个进程至少有一个线程，可以有多个线程。
多线程的调度由操作系统控制，多线程的优点是可以充分利用程序的空闲时间。
程序等待O、网络等资源的时候，如果有多线程就可以避免等待时浪费时间。

Java 内置对多线程的支持。Java 中使用 Thread 类表示线程，每个线程对应一个
Thread 类的实例。

ps：Thread 和 Runnable 接口都是 java.lang 包里的。
## 创建线程的方法
Java 提供两种创建线程的方法：
- 实现 Runnable 接口
- 扩展 Thread 类
PS：Thread 也实现了 Runnable 接口，但扩展 Thread 类可以直接通过 start 方法启动线程，而不必把它传给另一个Thread对象，
所以这两种方法还是略有区别的。
PS：实现 Runnable 接口的方法可能更好一些，因为实现接口不妨碍再扩展别的类。

无论哪种方法，都要创建
### 通过实现 Runnable 接口创建线程
```java
{{#include ../code/basic/ch11-thread/DefThreadA.java}}
```
- Runnable 接口只有一个方法 void run(), run 方法是线程逻辑的入口，run方法执行完毕后，线程也就结束了。
- 通过把实现了 Runnable 接口的对象传递个 Thread 构造函数创建线程。第二个参数是线程名称，可以省略，然后JVM会自动分配一个唯一的线程名称。
- Thread 的 start 方法是启动线程的，直到调用 start 方法，线程才会启动。start方法会立即返回。
- 在这个程序中，为了演示主线程和 th 并发执行，有意控制线程的执行时间，并使主线程在 th 结束之后才结束。

  - 实际上，Java 会在所有线程都结束之后才结束运行，所以使主线程最后结束不是必须的，
    但这是一个应该遵循的良好编程习惯。
  - 使主线程在所有线程结束之后才结束，有更好的方法确保这一点。这个程序通过控制线程执行时间，不够可靠，但用来演示这个简单的
    例子足够了

### 通过继承 Thread 类创建线程

```java
{{#include ../code/basic/ch11-thread/DefThreadB.java}}
```
这种方法用的比较少
## 创建线程的常用模式

```java
{{#include ../code/basic/ch11-thread/DefThreadC.java}}
```

特点：
- 设置一个私有变量，保存线程，构造函数中自动创建线程并初始化它。
- 定义一个静态方法，创建实例，并启动线程。这比较方便，在自己的基于线程的程序中使用这种方式也很有帮助。
## 等到线程结束
前面的示例中，为了使主线程最后结束，有意控制了线程执行时间，这并不靠谱，Java 提供了
专门的API完成这件事。

一种方式是通过线程的在循环中周期性检查线程的 isAlive 方法判断线程是否终止
```java
Thread th = new MyThread();
th.start();
do {
    try {
        Thread.sleep(100);
    } catch (InterruptedException e){
        System.out.println("Main thread interrupted");
    }
} while (th.isAlive());

```
另一种方式是使用 Thead.join() 方法，等待线程结束，这个方法的重载版本允许指定等待的最长时间。

```java
{{#include ../code/basic/ch11-thread/WatiThread.java}}
```
## 线程的优先级
Java 中线程优先级取值范围是 Thread.MAX_PRIORITY 和 Thread.MIN_PRIORITY 之间，默认优先级是 Thread.NORM_PRIORITY。

```java
{{#include ../code/basic/ch11-thread/PriorityDemo.java}}
```

但是在 WSL2 下运行，高优先级的并没有压倒性的优势
```
$ javac PriorityDemo.java  && taskset 1 java PriorityDemo
t2 优先级是：3
t1 优先级是：7
t4 优先级是：5
t3 优先级是：5
t2      : terminated
t3      : terminated
t1      : terminated
t4      : terminated

t1(high)   thread count to 6128484
t2(low)    thread count to 6280207
t3(normal) thread count to 7287721
t4(normal) thread count to 10000000
Main thread terminated.

$ javac PriorityDemo.java  && taskset 1 java PriorityDemo
t1 优先级是：7
t2 优先级是：3
t3 优先级是：5
t4 优先级是：5
t3      : terminated
t1      : terminated
t4      : terminated
t2      : terminated

t1(high)   thread count to 8366087
t2(low)    thread count to 10000000
t3(normal) thread count to 7409486
t4(normal) thread count to 5945962
Main thread terminated.

```

## 其它
- 线程有一个唯一的id和一个名称，id是在线程创建的时候分配到且不可变，名称可以在创建
  的时候指定，如果不指定就会自动分配，也可以在创建线程后修改。线程的名称可以不唯一。
- 线程的状态
- 线程组：线程组是一组线程，线程组可以包含其他线程组，从而构成树状结构，每个线程组都有一个父线程组，除了根。
- 守护线程：当所有非守护线程都结束后，JVM就会结束。守护线程创建的线程自动是守护线程，非守护线程
  创建的线程自动是非守护线程。可以用 Thread.setDaemon() 把线程设置为守护线程。
  JVM 启动时有且仅有一个非守护线程。
- 线程优先级：新创建的线程默认与当前线程具有同样的优先级
