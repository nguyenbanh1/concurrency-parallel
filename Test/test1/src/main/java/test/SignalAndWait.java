package test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SignalAndWait {
    MonitorObject myMonitorObject = new MonitorObject();
    boolean wasSignalled = false;

    public void doWait(){
        synchronized(myMonitorObject){
            while (!wasSignalled){
                try{
                    myMonitorObject.wait();
                } catch(InterruptedException e){
                    log.error(e.getMessage(), e);
                }
            }
            //clear signal and continue running.
            wasSignalled = false;
        }
    }

    public void doNotify(){
        synchronized(myMonitorObject){
            wasSignalled = true;
            myMonitorObject.notify();
        }
    }

    public void doNotifyAll(){
        synchronized(myMonitorObject){
            wasSignalled = true;
            myMonitorObject.notifyAll();
        }
    }
}
