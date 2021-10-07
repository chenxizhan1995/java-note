public class TickTockDemo{
    public static void main(String[] args){
        TickTock tt = new TickTock();
        MyThread tick = MyThread.createAndStart("Tick", tt);
        MyThread tock = MyThread.createAndStart("Tock", tt);

        try {
            tick.thread.join();
            tock.thread.join();
        } catch (InterruptedException e){
            System.out.println("Main thread interrupted.");
        }
        System.out.println("Main thread terminated");
    }
}


class TickTock{
    String state;

    public synchronized void tick(boolean running){
        if (!running){
            state = "Ticked";
            notify();
            return;
        }
        System.out.print("Tick");
        state = "Ticked";
        notify();
        try {
            while(!"Tocked".equals(state)) wait();
        } catch (InterruptedException e){
            System.out.println(Thread.currentThread().getName() + " interrupted!");
        }
    }

    public synchronized void tock(boolean running){
        if (!running){
            state = "Tocked";
            notify();
            return;
        }
        System.out.println(" Tock");
        state = "Tocked";
        notify();
        try {
            while(!"Ticked".equals(state)) wait();
        } catch (InterruptedException e){
            System.out.println(Thread.currentThread().getName() + " interrupted!");
        }
    }
}
class MyThread implements Runnable {
    Thread thread;
    TickTock tt;
    public MyThread(String tickTock, TickTock tt){
        thread = new Thread(this, tickTock);
        this.tt = tt;
    }
    public static MyThread createAndStart(String name, TickTock tt){
        MyThread th = new MyThread(name, tt);
        th.thread.start();
        return th;
    }
    @Override
    public void run(){
        if (thread.getName().equals("Tick")){
            for (int i = 0; i<5; i++) tt.tick(true);
            tt.tick(false);
        } else {
            for (int i = 0; i<5; i++) tt.tock(true);
            tt.tock(false);
        }
    }

}
