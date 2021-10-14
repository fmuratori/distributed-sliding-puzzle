package part1.actor;

import akka.actor.AbstractActor;

public class ViewActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return null;
    }
//    @Override
//    public Receive createReceive() {
//        return receiveBuilder().match(PressedMsg.class, msg -> {
//            System.out.println("Pressed!");
//        }).build();
//    }
}

