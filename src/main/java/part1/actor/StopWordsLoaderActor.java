package part1.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.FileReader;
import part1.message.LoadStopWordsReqMessage;
import part1.message.LoadStopWordsResMessage;
import part1.message.Message;

import java.util.LinkedList;
import java.util.List;

public class StopWordsLoaderActor extends AbstractBehavior<Message> {

    public StopWordsLoaderActor(ActorContext<Message> context) {
        super(context);
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(StopWordsLoaderActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(LoadStopWordsReqMessage.class, this::onLoadStopWordsReqMessage)
                .build();
    }

    private Behavior<Message> onLoadStopWordsReqMessage(LoadStopWordsReqMessage message) {
        // carica stopwords
        List<String> stopWords = FileReader.getStopWords(message.file);

        // UpdateStopWordsMessage
        message.caller.tell(new LoadStopWordsResMessage(stopWords));
        return this;
    }
}

