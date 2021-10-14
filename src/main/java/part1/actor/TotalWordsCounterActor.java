package part1.actor;

import akka.actor.AbstractActor;
import part1.message.ProcessFileResMessage;

public class TotalWordsCounterActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ProcessFileResMessage.class, msg -> {

            System.out.println("ASD");

        }).build();
    }
}

