import com.lmax.disruptor.EventHandler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class OffsetCallback {

    public static final int MAX = 10;
    public static final AtomicBoolean isStop = new AtomicBoolean();
    private static final AtomicInteger count = new AtomicInteger();
    private static final AtomicReferenceArray<Offset> offset = new AtomicReferenceArray(MAX);

    public static void callbackOffset(Long sequenceId) {
        offset.set(sequenceId.intValue() % offset.length(), new Offset(sequenceId, false));
    }

    public static void completeToOffset(long sequenceId) {
        offset.set((int) sequenceId % offset.length(), new Offset(sequenceId, true));
        count.getAndIncrement();
        if (count.get() == MAX) {
            StringBuffer data = new StringBuffer();
            for (int i = 0; i < MAX; i++) {
                data.append(", " + offset.get(i).sequenceId);
            }
            FileWriter.writeOffsetBack(data.toString());
            count.set(0);
            isStop.set(false);
        }

    }

    public static boolean isFull() {
        return count.get() == offset.length();
    }

    public static EventHandler<Event.ValueEvent>[] getEventHandler() {
        EventHandler<Event.ValueEvent> eventHandler = (event, sequence, endOfBatch)
                -> callbackOffset(sequence);
        return new EventHandler[]{eventHandler};
    }

    public static class Offset {
        public long sequenceId;
        public boolean isComplete;

        public Offset(long sequenceId, boolean isComplete) {
            this.sequenceId = sequenceId;
            this.isComplete = isComplete;
        }
    }
}
