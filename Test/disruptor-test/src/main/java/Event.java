import com.lmax.disruptor.EventFactory;
import lombok.Getter;
import lombok.Setter;

public class Event {

    @Getter
    @Setter
    public static class ValueEvent {
        private int value;
        public final static EventFactory EVENT_FACTORY = () -> new ValueEvent();
    }

}
