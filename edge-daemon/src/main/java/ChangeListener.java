import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

public abstract class ChangeListener {
 
    Kind[] eventTypes = null;
    public ChangeListener(Kind...eventTypes) {
        this.eventTypes = eventTypes;
    }
    public Kind[] getEventTypes() {
        return eventTypes;
    }
    public abstract void onEvent(WatchEvent<Path> anEvent);
 
}