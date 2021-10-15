package part1.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.FileReader;
import part1.message.Message;
import part1.message.ProcessFileReqMessage;
import part1.message.ProcessFileResMessage;

import java.util.List;

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

    /**
     * @param message
     * @return lista di parole contenute nel file al path passato tramite messaggio
     */
    private Behavior<Message> onProcessFileReqMessage(ProcessFileReqMessage message) {
        List<String> wordList = FileReader.getWordsFromPdf(message.getFilePath());

        message.getCaller().tell(new ProcessFileResMessage(message.getFilePath(), wordList));

        return this;
    }

}

