package part1.message;

import akka.actor.typed.ActorRef;

import java.util.List;

public class ProcessFileReqMessage implements Message {

    private final String filePath;
    private final List<String> stopWords;
    private final ActorRef<Message> caller;

    public ProcessFileReqMessage(String filePath, List<String> stopWords, ActorRef<Message> caller) {
        this.filePath = filePath;
        this.stopWords = stopWords;
        this.caller = caller;
    }

    public String getFilePath() {
        return filePath;
    }

    public ActorRef<Message> getCaller() {
        return caller;
    }

}
