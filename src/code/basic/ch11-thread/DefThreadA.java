/**
创建线程的方法1:实现 Runnable接口
*/

public class DefThreadA{
    public static void main(String[] args){
        MyThreadA my = new MyThreadA();
        Thread th = new Thread(my, "MyThread");
        th.start();

        for (int i = 0; i<50; i++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e){
                System.out.println("Main thread Interrupted.");
            }
            System.out.print(".");
        }
        System.out.println("Main Thread terminated");
    }
}

/**
这个线程每400ms输出一条计数语句，连续输出 10 次
*/
class MyThreadA implements Runnable{

    @Override
    public void run(){
        String threadName = Thread.currentThread().getName();
        try {
            for (int i = 0; i<10; i++){
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

$ java DefThreadA
....In MyThread: count = 0
....In MyThread: count = 1
....In MyThread: count = 2
....In MyThread: count = 3
....In MyThread: count = 4
....In MyThread: count = 5
....In MyThread: count = 6
....In MyThread: count = 7
....In MyThread: count = 8
....In MyThread: count = 9
MyThread terminated.
..........Main Thread terminated
*/
