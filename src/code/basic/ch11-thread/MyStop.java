/**
自己实现线程的挂起、恢复、终止
*/

public class MyStop{
    public static void main(String[] args){
        MyThread th = MyThread.createAndStart("my thread");
        try {
            Thread.sleep(1000);
            th.suspend();
            System.out.println("suspending thread");

            Thread.sleep(1000);
            th.resume();
            System.out.println("resume thread");

            Thread.sleep(1000);
            th.suspend();
            System.out.println("suspending thread");

            Thread.sleep(1000);
            th.stop();
            System.out.println("stop thread");


        } catch (InterruptedException e){
            System.out.println("Main thread interrupted");
        }

        try {
            th.thread.join();
        } catch (InterruptedException e){
            System.out.println("Main thread interrupted");
        }
        System.out.println("Main thread terminated");


    }
}

class MyThread implements Runnable{
    Thread thread;
    private boolean suspend;
    private boolean stop;
    public MyThread(String name){
        thread = new Thread(this, name);
        suspend = stop = false;
    }

    public static MyThread createAndStart(String name){
        MyThread t = new MyThread(name);
        t.thread.start();
        return t;
    }

    @Override
    public void run(){
        try {
            for (int i = 0; i< 1000; i++){
                System.out.print(i + " ");
                if (i%10 == 9){
                    System.out.println();
                }
                Thread.sleep(250);
                synchronized(this){
                    while (suspend){ wait(); }

                    if (stop){break;}
                }
            }
        } catch (InterruptedException e){
            System.out.println(thread.getName() + " interrupted");
        }
        System.out.println(thread.getName() + " exiting");
    }

    public synchronized void suspend(){
        this.suspend = true;
    }
    public synchronized void resume(){
        this.suspend = false;
        notify();
    }
    public synchronized void stop(){
        this.stop = true;

        // 这两行确保挂起的线程也可以终止
        this.suspend = false;
        notify();
    }

}

/**
$ javac MyStop.java  && java MyStop
0 1 2 3 suspending thread
resume thread
4 5 6 7 suspending thread
stop thread
my thread exiting
Main thread terminated
*/
