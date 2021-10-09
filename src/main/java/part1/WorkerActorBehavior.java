package part1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.messages.JobMessage;
import part1.messages.JobRequestMessage;

import java.util.ArrayList;

public class WorkerActorBehavior extends AbstractBehavior<JobMessage> {

    public static Behavior<JobMessage> create() {
        return Behaviors.setup(WorkerActorBehavior::new);
    }

    private WorkerActorBehavior(ActorContext<JobMessage> context) {
        super(context);
    }

    @Override
    public Receive<JobMessage> createReceive() {
        return newReceiveBuilder().onMessage(JobRequestMessage.class, this::onJobRequestMessage).build();
    }

    private Behavior<JobMessage> onJobRequestMessage(JobRequestMessage msg) {
        String filePath = msg.getFilePath();
        FileReader fr = new FileReader();
        ArrayList<String> wordList = fr.getWordsFromPdf(filePath);

        System.out.println(wordList.size());

        return this;
    }

}
