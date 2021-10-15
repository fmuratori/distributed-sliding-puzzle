package part1.message;

import akka.actor.typed.ActorRef;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ProcessFileResMessage implements Message {

    public String filePath;
    public List<String> wordList;

    public ProcessFileResMessage(String filePath, List<String> wordList) {
        this.filePath = filePath;
        this.wordList = wordList;
    }
}
