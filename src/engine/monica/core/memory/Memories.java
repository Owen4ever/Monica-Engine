package engine.monica.core.memory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Set;

public class Memories {

    public Memories() {
    }

    public void addMemory(BigInteger datetime, IEvent event) {
        if (datetime == null)
            throw new NullPointerException("The date time is null.");
        if (event == null)
            throw new NullPointerException("The event is null.");
        memories.put(datetime, event);
    }

    public Set<BigInteger> getMemoryDateTimes() {
        return memories.keySet();
    }

    public IEvent getMemory(BigInteger datetime) {
        if (datetime == null)
            throw new NullPointerException("The date time is null.");
        return memories.get(datetime);
    }
    private final HashMap<BigInteger, IEvent> memories = new HashMap<>();
}
