package part1.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.FileReader;
import part1.message.*;

import java.util.*;

/**
 * istanzia attori worker, ognuno dei quali analizza un pdf diverso
 * i risultati vengono convogliati nel TaskActor
 */
public class TaskActor extends AbstractBehavior<Message> {

    public static int NUM_WORKERS = Runtime.getRuntime().availableProcessors()+1;

    private ActorRef<Message> uiActor;
    private final List<ActorRef<Message>> workerActors = new ArrayList<>();

    private List<String> stopWords = new ArrayList<>();
    private List<String> filePathList = new ArrayList<>();
    private int numFiles = 0;

    private Boolean stopped = false;

    private LinkedHashMap<String, Integer> wordCountMap = new LinkedHashMap<>();
    private Integer totalWordsCount = 0;
    private Integer wordNumber;

    public TaskActor(ActorContext<Message> context) {
        super(context);
        for (int i = 0; i < NUM_WORKERS; i++) {
            workerActors.add(getContext().spawnAnonymous(FileWordsCounterActor.create()));
        }
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(TaskActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartTaskReqMessage.class, this::onStartTaskMessage)
                .onMessage(ProcessFileResMessage.class, this::onProcessFileResMessage)
                .onMessage(StopTaskMessage.class, this::onStopTaskMessage)
                .build();
    }

    private Behavior<Message> onStartTaskMessage(StartTaskReqMessage message) {
        //carico lista di parole bandite e path di file da analizzare attraverso il fileReader
        this.filePathList = FileReader.getFilesInFolder(message.getFolder(), "pdf");
        this.wordNumber = message.getWordNumber();
        this.uiActor = message.getCaller();
        this.wordCountMap = new LinkedHashMap<>();
        this.stopped = false;

        this.stopWords = FileReader.getWordsFromPdf(message.getBannedWordsFile());

        numFiles = this.filePathList.size();

        //per rendere le stop word case-insensitive le porto tutte in lowercase
        int index = 0;
        for(String word : this.stopWords){
            this.stopWords.set(index, word.toLowerCase());
            index++;
        }

        workerActors.forEach(w -> {
            if (this.filePathList.size() > 0) {
                String elem = this.filePathList.remove(0);
                w.tell(new ProcessFileReqMessage(elem, stopWords, getContext().getSelf()));
            }
        });
        return this;
    }

    private Behavior<Message> onStopTaskMessage(StopTaskMessage message) {
        this.stopped = true;
        return this;
    }

    private Behavior<Message> onProcessFileResMessage(ProcessFileResMessage message) {
        addWords(this.wordCountMap, message.getWordList());
        List<String[]> result = getOrderedResult(this.wordCountMap, this.wordNumber);
        if (!this.stopped) {
            this.uiActor.tell(new TaskUpdateMessage(result, totalWordsCount, numFiles));

            if (this.filePathList.size() > 0) {
                String elem = this.filePathList.remove(0);
                message.getActor().tell(new ProcessFileReqMessage(elem, stopWords, getContext().getSelf()));
            }
        }
        return this;
    }

    /**
     * @param wordMap hash map da aggiornare
     * @param wordList lista di parole da inserire nella hash map
     */
    private void addWords(LinkedHashMap<String,Integer> wordMap, List<String> wordList){
        wordList.forEach(word -> {
            //verifica sulle stopwords case insensitive
            if (!stopWords.contains(word.toLowerCase())){
                totalWordsCount += 1;
                if(wordMap.containsKey(word))
                    wordMap.replace(word, wordMap.get(word)+1);
                else
                    wordMap.put(word,1);
            }
        });
    }

    /**
     * @param wordMap mappa parole-numeroOccorrenze da ordinare e tagliare
     * @param wordNumber numero di parole da prelevare
     * @return restituisce la lista delle parole più frequenti
     */
    private List<String[]> getOrderedResult(LinkedHashMap<String,Integer> wordMap, Integer wordNumber){
        List<String[]> result = new ArrayList<>();

        wordMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(wordNumber).forEach(entry -> {
                    String[] newRow = {entry.getKey(), entry.getValue().toString()};
                    result.add(newRow);
                });

        return result;
    }

}

