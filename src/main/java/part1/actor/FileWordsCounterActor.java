package part1.actor;

import akka.actor.AbstractActor;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.FileReader;
import part1.WordsCounter;
import part1.message.FindFilesResMessage;
import part1.message.Message;
import part1.message.ProcessFileReqMessage;
import part1.message.ProcessFileResMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileWordsCounterActor extends AbstractBehavior<Message> {

    public FileWordsCounterActor(ActorContext<Message> context) {
        super(context);
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(FileWordsCounterActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(ProcessFileReqMessage.class, this::onProcessFileReqMessage)
                .build();
    }

    private Behavior<Message> onProcessFileReqMessage(ProcessFileReqMessage message) {
        String text = FileReader.getPdfText(message.file);

        List<String> words = WordsCounter.processText(text);

        Map<String, Integer> counts = WordsCounter.countWords(words);

        WordsCounter.filterStopWords(counts, message.stopWords);

        message.caller.tell(new ProcessFileResMessage(message.file, getContext().getSelf(), counts));
        return this;
    }
}

