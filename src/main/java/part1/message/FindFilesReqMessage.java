package part1.message;

import akka.actor.typed.ActorRef;

public class FindFilesReqMessage implements Message {
    public String path;
    public ActorRef<Message> caller;

    public FindFilesReqMessage(String path, ActorRef<Message> caller) {
        this.path = path;
        this.caller = caller;
    }
}
