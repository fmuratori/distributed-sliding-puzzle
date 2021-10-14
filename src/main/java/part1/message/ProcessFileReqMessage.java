package part1.message;

import akka.actor.typed.ActorRef;

import java.io.File;
import java.util.List;

public class ProcessFileReqMessage implements Message {

    public File file;
    public List<String> stopWords;
    public ActorRef<Message> caller;

    public ProcessFileReqMessage(File file, List<String> stopWords, ActorRef<Message> caller) {
        this.file = file;
        this.stopWords = stopWords;
        this.caller = caller;
    }
}
