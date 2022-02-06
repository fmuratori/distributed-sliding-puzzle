package part1.message;

import java.util.List;

public class TaskUpdateMessage implements Message {

    //lista di doppie parola-numeroOccorrenze ordinate per numero di occorrenze
    private final List<String[]> wordList;
    private final Integer totalWordsCount;
    private final Integer totalFileNumber;

    public TaskUpdateMessage(List<String[]> wordList, Integer totalWordsCount, Integer totalFileNumber) {
        this.wordList = wordList;
        this.totalWordsCount = totalWordsCount;
        this.totalFileNumber = totalFileNumber;
    }

    public List<String[]> getWordList() {
        return wordList;
    }

    public Integer getTotalWordsCount() {
        return totalWordsCount;
    }

    public Integer getTotalFileNumber() {
        return totalFileNumber;
    }

}
