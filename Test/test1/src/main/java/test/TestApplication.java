package test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@State(Scope.Benchmark)
public class TestApplication {

    Lock lock = new ReentrantLock();

    @Param({ "100", "200", "300", "500", "1000" })
    public int iterations;

    public volatile int count = 0;

    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        TestApplication application = new TestApplication();
        SignalAndWait signalAndWait = new SignalAndWait();

        Runnable runnable1 = () -> {
            System.out.println("await runnable 1");
            signalAndWait.doWait();
            System.out.println("runnable 1");
        };

        Runnable runnable2 = () -> {
            System.out.println("await runnable 2");
            signalAndWait.doWait();
            System.out.println("runnable 2");
        };

        Runnable runnable = () -> {
            while (application.count != 1000000000) {
                application.add();
            }
            System.out.println("Do notify");
            signalAndWait.doNotifyAll();
        };

        Thread a = new Thread(runnable);
        Thread b = new Thread(runnable1);
        Thread c = new Thread(runnable2);

        a.start();
        b.start();
        c.start();

        a.join();
        b.join();
        c.join();
        System.out.println(application.count);
    }

    @Benchmark
    public void add() {
        count++;
    }

//    public static EventHandler<Event.ValueEvent>[] getEventHandler() {
//        EventHandler<Event.ValueEvent> eventHandler = (event, sequence, endOfBatch)
//                -> process(event.getValue(), sequence);
//        return new EventHandler[]{eventHandler};
//    }

//    public static void process(int id, long sequence) {
//        System.out.println("Thread: " + Thread.currentThread().getName());
//        System.out.println("Id : " + id + " - seq: " + sequence);
//    }

}
