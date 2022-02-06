package part1;

import akka.actor.typed.ActorSystem;
import part1.actor.*;
import part1.message.Message;
import part1.ui.GraphicUI;

public class Main {

    public static void main(String[] args) {
        final ActorSystem<Message> system =
                ActorSystem.create(UIActor.create(), "task");

        new GraphicUI(system);
    }

}
