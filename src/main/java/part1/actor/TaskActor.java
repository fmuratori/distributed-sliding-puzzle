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

    private static final int WORKERS_POOL_SIZE = 5;

    private final ActorRef stopWordsLoader;
    private final ActorRef filesFinder;
    private final List<ActorRef> wordCounters;
    private List<String> stopWords = new ArrayList<>();
    private Map<String, Integer> counts = new HashMap<>();

    private List<File> unprocessedFiles = new ArrayList<>();

    public TaskActor(ActorContext<Message> context) {
        super(context);

        stopWordsLoader =
                getContext().spawnAnonymous(StopWordsLoaderActor.create());

        filesFinder =
                getContext().spawnAnonymous(FilesFinderActor.create());

        wordCounters = new ArrayList<>();
        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
            wordCounters.add(getContext().spawnAnonymous(FileWordsCounterActor.create()));
        }
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(TaskActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartTaskMessage.class, this::onStartTaskMessage)
                .onMessage(LoadStopWordsResMessage.class, this::onLoadStopWordsResMessage)
                .onMessage(FindFilesResMessage.class, this::onFindFilesResMessage)
                .onMessage(ProcessFileResMessage.class, this::onProcessFileResMessage)
                .build();
    }

    private Behavior<Message> onFindFilesResMessage(FindFilesResMessage message) {
        this.unprocessedFiles = message.files;
        this.checkComputationStart();
        return this;
    }

    private Behavior<Message> onLoadStopWordsResMessage(LoadStopWordsResMessage message) {
        this.stopWords = message.stopWords;
        this.checkComputationStart();
        return this;
    }

    private Behavior<Message> onStartTaskMessage(StartTaskMessage message) {
        // send messages LoadStopWordsReqMessage
        stopWordsLoader.tell(new LoadStopWordsReqMessage(message.stopWordsFile, getContext().getSelf()));

        // send messages FindFilesReqMessage
        filesFinder.tell(new FindFilesReqMessage(message.folder, getContext().getSelf()));

        return this;
    }

    private Behavior<Message> onProcessFileResMessage(ProcessFileResMessage message) {
        this.unprocessedFiles.remove(message.file);

        WordsCounter.sumCounters(this.counts, message.counts);

        System.out.println(this.counts);

        if (this.unprocessedFiles.size() > 0) {
            message.caller.tell(new ProcessFileReqMessage(
                    this.getNextFile(), stopWords, getContext().getSelf()));
        }
        return this;
    }

    private void checkComputationStart() {
        if (!this.unprocessedFiles.isEmpty() && !this.stopWords.isEmpty()) {

            System.out.println(this.unprocessedFiles);
            System.out.println(this.stopWords);

            for (ActorRef wordCounter : wordCounters) {
                System.out.println("A");
                System.out.println(this.getNextFile());
                System.out.println("B");

//                File f  = this.getNextFile();
//
//
//                wordCounter.tell(new ProcessFileReqMessage(
//                        this.getNextFile(), stopWords, getContext().getSelf()));
            }
        }
    }

    private File getNextFile() {
        return this.unprocessedFiles.remove(0);
    }
}

