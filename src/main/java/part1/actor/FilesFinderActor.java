package part1.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.FileReader;
import part1.message.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FilesFinderActor  extends AbstractBehavior<Message> {

    public FilesFinderActor(ActorContext<Message> context) {
        super(context);
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(FilesFinderActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(FindFilesReqMessage.class, this::onFindFilesReqMessage)
                .build();
    }

    private Behavior<Message> onFindFilesReqMessage(FindFilesReqMessage message) {
        ArrayList<String> filesName = FileReader.getFilesInFolder(message.path, "pdf");
        List<File> files = filesName.stream().map(File::new).toList();
        message.caller.tell(new FindFilesResMessage(files));
        return this;
    }
}

