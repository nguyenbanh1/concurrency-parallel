import java.util.concurrent.locks.Lock;

class worker implements Runnable {
    Lock re;
    Integer count;

    public worker(Lock rl, Integer count) {
        re = rl;
        this.count = count;
    }

    public void run() {
        boolean done = false;
        while (!done) {
            //Getting Outer Lock
            boolean ans = re.tryLock();
            // Returns True if lock is free
            if (ans) {
                re.lock();
                System.out.println(count);
                done = true;
                re.unlock();
            }
        }
    }
}