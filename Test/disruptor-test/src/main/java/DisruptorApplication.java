import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ThreadFactory;

@Slf4j
public class DisruptorApplication {

    public static void main(String[] args) {
        int max = 100;
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        WaitStrategy waitStrategy = new BusySpinWaitStrategy();
        Disruptor<Event.ValueEvent> disruptor
                = new Disruptor<>(
                Event.ValueEvent.EVENT_FACTORY,
                    2048,
                threadFactory,
                ProducerType.SINGLE,
                waitStrategy);
        EventHandler<Event.ValueEvent>[] handle0 = OffsetCallback.getEventHandler();
        EventHandler<Event.ValueEvent>[] handle1 = SingleEventPrintConsumer.getEventHandler();

        disruptor.handleEventsWith(handle0);
        disruptor.after(handle0).handleEventsWith(handle1);

        RingBuffer<Event.ValueEvent> ringBuffer = disruptor.start();
        Date start = new Date();

        int eventCount = 0;
        int time = 1;
        while (eventCount < max) {
//            if (OffsetCallback.isStop.get()) {
//                continue;
//            }
            long sequenceId = ringBuffer.next();
            Event.ValueEvent valueEvent = ringBuffer.get(sequenceId);
            valueEvent.setValue(eventCount);
            ringBuffer.publish(sequenceId);
//            if (eventCount + 1 == time * OffsetCallback.MAX) {
//                time++;
//                OffsetCallback.isStop.set(true);
//            }
            eventCount++;
        }
        while (SingleEventPrintConsumer.integer.get() < max) {}
        SingleEventPrintConsumer.executor.shutdown();
        Date end = new Date();
        log.info("length: " + SingleEventPrintConsumer.integer);
        log.info("Time: " + Duration.between(start.toInstant(), end.toInstant()).toMillis());
    }
}
