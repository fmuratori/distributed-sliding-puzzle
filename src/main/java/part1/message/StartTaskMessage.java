package part1.message;

import akka.actor.typed.ActorRef;

public class StartTaskMessage implements Message {

    public String folder;
    public String stopWordsFile;
//    public ActorRef<Message> caller;

//    public StartTaskMessage(String folder, String stopWordsFile, ActorRef<Message> caller) {
    public StartTaskMessage(String folder, String stopWordsFile) {
        this.folder = folder;
        this.stopWordsFile = stopWordsFile;
//        this.caller = caller;
    }
}
