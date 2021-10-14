package part1.message;

import akka.actor.typed.ActorRef;

public class StartTaskReqMessage implements Message {

    public String folder;
    public String stopWordsFile;
    public ActorRef<Message> caller;

    public StartTaskReqMessage(String folder, String stopWordsFile) {
        this.folder = folder;
        this.stopWordsFile = stopWordsFile;
    }

    public void setContext(ActorRef<Message> actorRef) {
        this.caller = actorRef;
    }
}
