package part1.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.WordsCounter;
import part1.message.*;

import java.io.File;
import java.util.*;

public class TaskActor extends AbstractBehavior<Message> {

    private ActorRef<Message> uiActor;
    private final ActorRef<Message> stopWordsLoader;
    private final ActorRef<Message> filesFinder;
    private List<ActorRef<Message>> counterActors;

    private Optional<List<String>> stopWords = Optional.empty();
    private Optional<List<File>> loadedFiles = Optional.empty();

    private int processedFilesCount;
    private Map<String, Integer> counts;

    public TaskActor(ActorContext<Message> context) {
        super(context);

        stopWordsLoader =
                getContext().spawnAnonymous(StopWordsLoaderActor.create());

        filesFinder =
                getContext().spawnAnonymous(FilesFinderActor.create());
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(TaskActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartTaskReqMessage.class, this::onStartTaskMessage)
                .onMessage(LoadStopWordsResMessage.class, this::onLoadStopWordsResMessage)
                .onMessage(FindFilesResMessage.class, this::onFindFilesResMessage)
                .onMessage(ProcessFileResMessage.class, this::onProcessFileResMessage)
                .build();
    }

    private Behavior<Message> onStartTaskMessage(StartTaskReqMessage message) {
        // send messages LoadStopWordsReqMessage
        stopWordsLoader.tell(new LoadStopWordsReqMessage(message.stopWordsFile, getContext().getSelf()));

        // send messages FindFilesReqMessage
        filesFinder.tell(new FindFilesReqMessage(message.folder, getContext().getSelf()));

        this.uiActor = message.caller;

        return this;
    }

    private Behavior<Message> onLoadStopWordsResMessage(LoadStopWordsResMessage message) {
        this.stopWords = Optional.of(message.stopWords);
        this.checkComputationStart();
        return this;
    }

    private Behavior<Message> onFindFilesResMessage(FindFilesResMessage message) {
        this.loadedFiles = Optional.of(message.files);
        this.checkComputationStart();
        return this;
    }

    private Behavior<Message> onProcessFileResMessage(ProcessFileResMessage message) {

        this.counts = WordsCounter.sumCounters(this.counts, message.counts);

        System.out.println(this.counts);

        this.processedFilesCount ++;

        uiActor.tell(new TaskUpdateMessage(this.counts));

        if (this.loadedFiles.isPresent() && this.processedFilesCount == this.loadedFiles.get().size()) {
            System.out.println("COMPLETED");


            // delete single actors (not just this actore reference to them)
            for (ActorRef<Message> counterActor : counterActors)
                getContext().stop(counterActor);

            // termiante the actor system entirely
//            getContext().getSystem().terminate();
        }

        return this;
    }

    private void checkComputationStart() {
        if (this.loadedFiles.isPresent() && this.stopWords.isPresent()) {

            this.counts = new HashMap<>();
            this.processedFilesCount = 0;

            counterActors = new ArrayList<>();
            for (File f : this.loadedFiles.get()) {
                ActorRef<Message> counterActor = getContext().spawnAnonymous(FileWordsCounterActor.create());
                counterActor.tell(new ProcessFileReqMessage( f, stopWords.get(), getContext().getSelf()));
                counterActors.add(counterActor);
            }
        }
    }
}

