package part1.message;


import akka.actor.typed.ActorRef;

import java.util.List;

public class ProcessFileResMessage implements Message {

    private final String filePath;
    private final List<String> wordList;
    private final ActorRef<Message> actor;

    public ProcessFileResMessage(String filePath, List<String> wordList, ActorRef<Message> actor) {
        this.filePath = filePath;
        this.wordList = wordList;
        this.actor = actor;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public ActorRef<Message> getActor() {
        return actor;
    }
}
