package part2.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part2.message.Message;
import part2.message.UIInitializedMessage;

public class GameActor extends AbstractBehavior<Message> {
//    private final ActorRef<Message> taskActor;

    public GameActor(ActorContext<Message> context) {
        super(context);
//        taskActor = getContext().spawnAnonymous(TaskActor.create());
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(GameActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(UIInitializedMessage.class, this::onUIInitializedMessage)
//                .onMessage(TaskUpdateMessage.class, this::onGameUpdateMessage)
                .build();
    }

    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        System.out.println("View ready");
        return this;
    }

//    private Behavior<Message> onGameUpdateMessage(TaskUpdateMessage message) {
//        return this;
//    }


}
