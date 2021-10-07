/**
创建线程的方法1:实现 Runnable接口
*/

public class DefThreadC{

    public static void main(String[] args){
        MyThreadC my = MyThreadC.createAndStart("th-c");

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
class MyThreadC implements Runnable{
    private Thread th;
    public MyThreadC(String name){
        this.th = new Thread(this, name);
    }
    public static MyThreadC createAndStart(String name){
        MyThreadC t = new MyThreadC(name);
        t.th.start();
        return t;
    }
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
$ java DefThreadC
....In th-c: count = 0
....In th-c: count = 1
....In th-c: count = 2
....In th-c: count = 3
....In th-c: count = 4
....In th-c: count = 5
....In th-c: count = 6
....In th-c: count = 7
....In th-c: count = 8
....In th-c: count = 9
th-c terminated.
..........Main Thread terminated

*/
