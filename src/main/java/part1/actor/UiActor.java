package part1.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.WordsCounter;
import part1.message.*;

import javax.swing.*;

public class UiActor  extends AbstractBehavior<Message> {

    private JTextArea textArea;
    private final ActorRef<Message> taskActor;


    public UiActor(ActorContext<Message> context) {
        super(context);

        taskActor = getContext().spawnAnonymous(TaskActor.create());
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(UiActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(UIInitializedMessage.class, this::onUIInitializedMessage)
                .onMessage(StartTaskReqMessage.class, this::onStartTaskReqMessage)
                .onMessage(TaskUpdateMessage.class, this::onTaskUpdateMessage)
                .build();
    }

    private Behavior<Message> onTaskUpdateMessage(M message) {
        textArea.setText(WordsCounter.stringifyMap(WordsCounter.sortMapByValue(message.counts), 10));
        return this;
    }

    private Behavior<Message> onStartTaskReqMessage(StartTaskReqMessage message) {
        message.setContext(getContext().getSelf());
        taskActor.tell(message);
        return this;
    }

    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        this.textArea = message.textArea;
        return this;
    }
}
