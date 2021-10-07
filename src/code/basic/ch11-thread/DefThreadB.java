/**
通过扩展 Thread 类创建线程
2021-10-07
*/

public class DefThreadB{
    public static void main(String[] args){
        Thread th = new MyThreadB();
        th.setName("mythread");
        th.start();

        for (int i  = 0; i<50; i++){
            System.out.print(".");
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                System.out.println("main thread interrupted");
            }
        }
        System.out.println("Main thread terminated");

    }
}

class MyThreadB extends Thread{
    @Override
    public void run(){
        final String threadName = Thread.currentThread().getName();

        try {
            for (int i = 0; i< 4; i++){
                System.out.println("In " + threadName + " : count = " + i);
                Thread.sleep(400);
            }
        } catch (InterruptedException e){
            System.out.println("Thread " + threadName + " interrupted");
        }
        System.out.println("Thread " + threadName + " terminated");
    }
}

/**
$ java DefThreadB
.In mythread : count = 0
....In mythread : count = 1
....In mythread : count = 2
....In mythread : count = 3
....Thread mythread terminated

*/
