package part1.message;

import java.util.List;

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
