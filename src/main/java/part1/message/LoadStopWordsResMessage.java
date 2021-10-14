package part1.message;

import java.util.List;

public class LoadStopWordsResMessage implements Message {

    public List<String> stopWords;

    public LoadStopWordsResMessage(List<String> stopWords) {
        this.stopWords = stopWords;
    }
}
