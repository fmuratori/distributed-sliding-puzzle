package part1.message;

import akka.actor.typed.ActorRef;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ProcessFileResMessage implements Message {

    private String filePath;
    private List<String> wordList;

    public ProcessFileResMessage(String filePath, List<String> wordList) {
        this.filePath = filePath;
        this.wordList = wordList;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<String> getWordList() {
        return wordList;
    }
}
