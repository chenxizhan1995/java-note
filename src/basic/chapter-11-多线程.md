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
<details><summary>点击展开代码</summary>

```java
{{#include ../code/basic/ch11-thread/DefThreadA.java}}
```
</details>

- Runnable 接口只有一个方法 void run(), run 方法是线程逻辑的入口，run方法执行完毕后，线程也就结束了。
- 通过把实现了 Runnable 接口的对象传递个 Thread 构造函数创建线程。第二个参数是线程名称，可以省略，然后JVM会自动分配一个唯一的线程名称。
- Thread 的 start 方法是启动线程的，直到调用 start 方法，线程才会启动。start方法会立即返回。
- 在这个程序中，为了演示主线程和 th 并发执行，有意控制线程的执行时间，并使主线程在 th 结束之后才结束。

  - 实际上，Java 会在所有线程都结束之后才结束运行，所以使主线程最后结束不是必须的，
    但这是一个应该遵循的良好编程习惯。
  - 使主线程在所有线程结束之后才结束，有更好的方法确保这一点。这个程序通过控制线程执行时间，不够可靠，但用来演示这个简单的
    例子足够了

### 通过继承 Thread 类创建线程
<details><summary>点击展开代码</summary>

```java
{{#include ../code/basic/ch11-thread/DefThreadB.java}}
```
</details>

这种方法用的比较少
## 创建线程的常用模式

<details><summary>点击展开代码</summary>

```java
{{#include ../code/basic/ch11-thread/DefThreadC.java}}
```
</details>

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

<details><summary>点击展开代码</summary>

```java
{{#include ../code/basic/ch11-thread/WaitThread.java}}
```
</details>

## 线程的优先级
Java 中线程优先级取值范围是 Thread.MAX_PRIORITY 和 Thread.MIN_PRIORITY 之间，默认优先级是 Thread.NORM_PRIORITY。

<details><summary>点击展开代码</summary>

```java
{{#include ../code/basic/ch11-thread/PriorityDemo.java}}
```
</details>

但是在 WSL2 下运行，高优先级的并没有压倒性的优势

<details><summary>点击展开代码</summary>

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
</details>

## 同步
使线程协调工作的方法称为同步（synchronization）。

java 中有同步方法（synchronized 修饰的方法）和同步语句（synchronized 限制的语句块），
任意时刻，同一个对象的所有同步方法只能有一个在执行，其它必须等待。

java 语言内置同步机制。所有对象都有一个锁，synchronized 语句用于获取对象的锁，
任意时刻只能有一个线程持有对象的锁，其它要获取锁的线程必须等待。

synchronized 方法执行前要先获取对象的锁，退出后会释放对象上的锁，从而确保只有一个线程执行。

## Demo:tick_tock
Object 定义了 wait 和 notify 方法，这些方法只能在 synchronized 环境下执行，wait 是放弃锁并
等待，notify 唤醒另一个等待此对象的锁的线程，被唤醒的线程直到当前线程释放锁之后才会开始执行。

这个例子，演示了在 synchronized 方法中两个线程通过 wait/notify 通信，协作输出 Tick Tock。

<details><summary>点击展开代码</summary>

```java
{{#include ../code/basic/ch11-thread/TickTockDemo.java}}
```
</details>

tick 和 tock 方法都有一段如下代码，这是必须的。只要考虑tick和tock的末尾都会执行 wait 方法，那么
当线程结束后，必然有一个线程处于 wait 状态，所以必须得有一个额外的notify唤醒它。
```java
if (!running){
    state = "Ticked";
    notify();
    return;
}
```
---
obj.wait() obj.notify() 只能在 synchronized 环境下使用……

  Exception in thread "Tick" Exception in thread "Tock" java.lang.IllegalMonitorStateException
```java
class TickTock {
    String state;
    Object o = new Object();
    public synchronized void tick(boolean running) {
        if (!running) {
            state = "Ticked";
            o.notify();
            return;
        }
        System.out.print("Tick");
        state = "Ticked";
        o.notify();
        try {
            while (!"Tocked".equals(state)) o.wait();
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " interrupted!");
        }
    }
}
```

## 线程的挂起和终止
Java 一开始提供了下述方法实现线程的挂起、继续执行、终止，但自Java 1.2 开始，都给废弃了。
- final void suspend()  有时会导致死锁和严重的系统错误
- final void resume()   本身没问题，但它是和 suspend 配合使用的，suspend 废弃了，它也就没有用了
- final void stop()     有时会导致严重的系统问题

替代方案是自己实现挂起和终止逻辑。比如通过设置标志变量，并在run中检测它。

```java
{{#include ../code/basic/ch11-thread/MyStop.java}}
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
