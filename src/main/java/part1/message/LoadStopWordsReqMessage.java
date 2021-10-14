package part1.message;

import akka.actor.typed.ActorRef;

public class LoadStopWordsReqMessage implements Message {
    public String file;
    public ActorRef<Message> caller;

    public LoadStopWordsReqMessage(String file, ActorRef<Message> caller) {
        this.file = file;
        this.caller = caller;
    }
}
