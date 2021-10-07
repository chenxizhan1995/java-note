/**
线程优先级示例
2021-10-07
*/
import java.util.concurrent.atomic.AtomicBoolean;
public class PriorityDemo{
    public static void main(String[] args){
        java.io.PrintStream out = System.out;

        MyThread t1 = new MyThread("t1");
        MyThread t2 = new MyThread("t2");
        MyThread t3 = new MyThread("t3");
        MyThread t4 = new MyThread("t4");

        t1.th.setPriority(Thread.NORM_PRIORITY + 2);
        t2.th.setPriority(Thread.NORM_PRIORITY - 2);

        System.out.println(t1.th.getName() + " 优先级是：" + t1.th.getPriority());
        System.out.println(t2.th.getName() + " 优先级是：" + t2.th.getPriority());
        System.out.println(t3.th.getName() + " 优先级是：" + t3.th.getPriority());
        System.out.println(t4.th.getName() + " 优先级是：" + t4.th.getPriority());

        t1.th.start();
        t2.th.start();
        t3.th.start();
        t4.th.start();
        try {
            t1.th.join();
            t2.th.join();
            t3.th.join();
            t4.th.join();
        } catch (InterruptedException e){
            System.out.println("main thread interrupted");
        }
        out.println();
        out.println("t1(high)   thread count to " + t1.count);
        out.println("t2(low)    thread count to " + t2.count);
        out.println("t3(normal) thread count to " + t3.count);
        out.println("t4(normal) thread count to " + t4.count);

        out.println("Main thread terminated.");
    }
}

class MyThread implements Runnable {
    private static AtomicBoolean stop = new AtomicBoolean(false);
    private static String currentName;

    final Thread th;
    int count;

    public MyThread(String name){
        this.th  = new Thread(this, name);
        this.count = 0;
        currentName = name;
    }

    @Override
    public void run(){
        // 从 0 往上数，第一个线程署到 1000 万后，所有线程都停止
        do {
            count++;
            if (currentName.compareTo(th.getName())!=0){
                currentName = th.getName();
                // System.out.println("In " + currentName);
            }
        } while (!stop.get() && count < 1_000_0000L);
        stop.set(true);
        System.out.println(th.getName() + "\t: terminated");
    }
}

/**
t1 优先级是：7
t2 优先级是：3
t3 优先级是：5
t4 优先级是：5
t1	: terminated
t3	: terminated
t2	: terminated
t4	: terminated

t1(high)   thread count to 10000000
t2(low)    thread count to 6434766
t3(normal) thread count to 6883011
t4(normal) thread count to 6209358
Main thread terminated.
[admin@hongkong ~]$ nproc
1

[admin@hongkong ~]$ javac PriorityDemo.java  && taskset 1 java PriorityDemo
t1 优先级是：7
t2 优先级是：3
t3 优先级是：5
t4 优先级是：5
t3      : terminated
t1      : terminated
t2      : terminated
t4      : terminated

t1(high)   thread count to 7643593
t2(low)    thread count to 8962908
t3(normal) thread count to 10000000
t4(normal) thread count to 7382586
Main thread terminated.
[admin@hongkong ~]$ javac PriorityDemo.java  && taskset 1 java PriorityDemo
t1 优先级是：7
t2 优先级是：3
t3 优先级是：5
t4 优先级是：5
t4      : terminated
t1      : terminated
t3      : terminated
t2      : terminated

t1(high)   thread count to 8730685
t2(low)    thread count to 8889758
t3(normal) thread count to 8604428
t4(normal) thread count to 10000000
Main thread terminated.
*/
