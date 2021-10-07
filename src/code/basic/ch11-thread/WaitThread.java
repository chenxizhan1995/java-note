
public class WaitThread{

    public static void main(String[] args){
        MyThread t1 = MyThread.createAndStart("t1");
        MyThread t2 = MyThread.createAndStart("t2");
        MyThread t3 = MyThread.createAndStart("t3");

        // 等待三个子线程结束
        try {
            t1.th.join();
            t2.th.join();
            t3.th.join();
        } catch (InterruptedException e){
            System.out.println("Main thread Interrupted.");
        }
        System.out.println("Main Thread terminated");
    }

}

/**
这个线程每400ms输出一条计数语句，连续输出 10 次
*/
class MyThread implements Runnable{
    final Thread th;
    public MyThread(String name){
        this.th = new Thread(this, name);
    }
    public static MyThread createAndStart(String name){
        MyThread t = new MyThread(name);
        t.th.start();
        return t;
    }
    @Override
    public void run(){
        String threadName = Thread.currentThread().getName();
        try {
            for (int i = 0; i<3; i++){
                Thread.sleep(400);
                System.out.println("In " + threadName + ": count = " + i);
            }
        } catch (InterruptedException e){
            System.out.println(threadName + " interrupted.");
        }
        System.out.println(threadName + " terminated.");

    }
}

/**
$ java WaitThread
In t1: count = 0
In t2: count = 0
In t3: count = 0
In t1: count = 1
In t3: count = 1
In t2: count = 1
In t1: count = 2
In t3: count = 2
In t2: count = 2
t2 terminated.
t3 terminated.
t1 terminated.
Main Thread terminated
*/
