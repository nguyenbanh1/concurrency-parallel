import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.ThreadFactory;

public class EvenNumberConsumer {

    private static ThreadFactory threadFactory1 = DaemonThreadFactory.INSTANCE;
    private static WaitStrategy waitStrategy1 = new BusySpinWaitStrategy();
    public static Disruptor<Event.ValueEvent> evenDisruptor
            = new Disruptor<>(
            Event.ValueEvent.EVENT_FACTORY,
            2048,
            threadFactory1,
            ProducerType.SINGLE,
            waitStrategy1);

    public static RingBuffer<Event.ValueEvent> start() {
        evenDisruptor.handleEventsWith(EvenNumberConsumer.getEventHandler());
        return evenDisruptor.start();
    }


    public static EventHandler<Event.ValueEvent>[] getEventHandler() {
        EventHandler<Event.ValueEvent> eventHandler = (event, sequence, endOfBatch)
                -> print(event.getValue(), sequence);
        return new EventHandler[]{eventHandler};
    }

    public static void print(int id, long sequenceId) {
        System.out.printf("EvenNumber: id = %s, sequence = %s%n", id, sequenceId);
        SingleEventPrintConsumer.integer.getAndIncrement();
    }
}
