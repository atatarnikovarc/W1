package bchat.src.model;

public class EventType {
    private int eventType;
    private Object argObject;

    public EventType(int eventType, Object argObject) {
        this.eventType = eventType;
        this.argObject = argObject;
    }

    public int getType() {
        return eventType;
    }

    public Object getArgObject() {
        return argObject;
    }
}