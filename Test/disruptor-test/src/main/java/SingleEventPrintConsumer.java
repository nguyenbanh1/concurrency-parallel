import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SingleEventPrintConsumer {

    public static AtomicInteger integer = new AtomicInteger();
    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static RingBuffer<Event.ValueEvent> oddRingBuffer = OddNumberConsumer.start();
    public static RingBuffer<Event.ValueEvent> evenRingBuffer = EvenNumberConsumer.start();

    public static EventHandler<Event.ValueEvent>[] getEventHandler() {
        EventHandler<Event.ValueEvent> eventHandler = (event, sequence, endOfBatch)
                -> process(event.getValue(), sequence);
        return new EventHandler[]{eventHandler};
    }

    private static void process(int id, long sequenceId) {
        if (id % 2 != 0) {
            long oddSequenceId = oddRingBuffer.next();
            Event.ValueEvent oddValueEvent = oddRingBuffer.get(oddSequenceId);
            oddValueEvent.setValue(id);
            oddRingBuffer.publish(oddSequenceId);
        } else {
            long evenSequenceId = evenRingBuffer.next();
            Event.ValueEvent evenValueEvent = evenRingBuffer.get(evenSequenceId);
            evenValueEvent.setValue(id);
            evenRingBuffer.publish(evenSequenceId);
        }
    }

    private static void print(int id, long sequenceId) {
        Worker worker = new Worker(id, sequenceId);
        executor.submit(worker);
    }

    public static class Worker implements Runnable {

        private Integer id;
        private long sequenceId;

        public Worker(Integer data, long sequenceId) {
            this.id = data;
            this.sequenceId = sequenceId;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://api.domainsdb.info/v1/domains/search?domain=google.com&zone=com")
                    .build();
            Call call = client.newCall(request);
            Response response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null && response.body() != null) {
                    response.body().close();
                }
            }
            OffsetCallback.completeToOffset(sequenceId);
            integer.getAndIncrement();
        }
    }
}
