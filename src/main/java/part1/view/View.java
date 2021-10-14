package part1.view;

import akka.actor.ActorRef;
import part1.actor.ViewActor;

public interface View {

    void start(ActorRef actor);
}
