package part1.message;

import akka.actor.typed.ActorRef;

public class StartTaskReqMessage implements Message {

    private String folder;
    private String bannedWordsFile;
    private Integer wordNumber;
    private ActorRef<Message> caller;

    public StartTaskReqMessage(String folder, String bannedWordsFile, Integer wordNumber, ActorRef<Message> caller) {
        this.folder = folder;
        this.bannedWordsFile = bannedWordsFile;
        this.wordNumber = wordNumber;
        this.caller = caller;
    }

    public String getFolder() {
        return folder;
    }

    public String getBannedWordsFile() {
        return bannedWordsFile;
    }

    public Integer getWordNumber() {
        return wordNumber;
    }

    public ActorRef<Message> getCaller() {
        return caller;
    }
}
