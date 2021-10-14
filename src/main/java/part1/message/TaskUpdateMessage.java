package part1.message;

import java.util.Map;

public class TaskUpdateMessage implements Message {

    public Map<String, Integer> counts;

    public TaskUpdateMessage(Map<String, Integer> counts) {
        this.counts = counts;
    }
}
