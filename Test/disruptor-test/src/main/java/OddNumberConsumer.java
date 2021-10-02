import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.ThreadFactory;

public class OddNumberConsumer {

    private static ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
    private static WaitStrategy waitStrategy = new BusySpinWaitStrategy();
    public static Disruptor<Event.ValueEvent> oddDisruptor
            = new Disruptor<>(
            Event.ValueEvent.EVENT_FACTORY,
            2048,
            threadFactory,
            ProducerType.SINGLE,
            waitStrategy);

    public static RingBuffer<Event.ValueEvent> start() {
        oddDisruptor.handleEventsWith(OddNumberConsumer.getEventHandler());
        return oddDisruptor.start();
    }

    public static EventHandler<Event.ValueEvent>[] getEventHandler() {
        EventHandler<Event.ValueEvent> eventHandler = (event, sequence, endOfBatch)
                -> print(event.getValue(), sequence);
        return new EventHandler[]{eventHandler};
    }

    public static void print(int id, long sequenceId) {
        System.out.printf("Odd Number: id = %s, sequence = %s%n", id, sequenceId);
        SingleEventPrintConsumer.integer.getAndIncrement();
    }
}
