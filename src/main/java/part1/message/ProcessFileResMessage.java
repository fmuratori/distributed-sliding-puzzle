package part1.message;

import akka.actor.typed.ActorRef;

import java.io.File;
import java.util.Map;

public class ProcessFileResMessage implements Message {

    public File file;
    public ActorRef<Message> caller;
    public Map<String, Integer> counts;

    public ProcessFileResMessage(File file, ActorRef<Message> caller, Map<String, Integer> counts) {
        this.file = file;
        this.caller = caller;
        this.counts = counts;
    }
}
