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

    private ActorRef<Message> uiActor;

    private LinkedHashMap<String, Integer> wordCountMap = new LinkedHashMap<>();
    private Integer totalWordsCount = 0;

    private Optional<List<String>> stopWords = Optional.empty();
    private Optional<List<String>> filePathList = Optional.empty();
    private Integer wordNumber;

    public TaskActor(ActorContext<Message> context) {
        super(context);
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(TaskActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartTaskReqMessage.class, this::onStartTaskMessage)
                .onMessage(ProcessFileResMessage.class, this::onProcessFileResMessage)
                .build();
    }

    private Behavior<Message> onStartTaskMessage(StartTaskReqMessage message) {
        //carico lista di parole bandite e path di file da analizzare attraverso il fileReader
        this.filePathList = Optional.of(FileReader.getFilesInFolder(message.getFolder(), "pdf"));
        this.stopWords = Optional.of(FileReader.getWordsFromPdf(message.getBannedWordsFile()));
        this.wordNumber = message.getWordNumber();
        this.uiActor = message.getCaller();

        filePathList.get().forEach(filePath -> {
            ActorRef<Message> worker = getContext().spawnAnonymous(FileWordsCounterActor.create());
            worker.tell(new ProcessFileReqMessage(filePath, stopWords.get(), getContext().getSelf()));
        });

        return this;
    }

    private Behavior<Message> onProcessFileResMessage(ProcessFileResMessage message) {
        this.totalWordsCount += message.getWordList().size();

        addWords(this.wordCountMap, message.getWordList());
        List<String[]> result = getOrderedResult(this.wordCountMap, this.wordNumber);

        this.uiActor.tell(new TaskUpdateMessage(result, totalWordsCount, filePathList.get().size()));
        return this;
    }

    /**
     * @param wordMap hash map da aggiornare
     * @param wordList lista di parole da inserire nella hash map
     */
    private void addWords(LinkedHashMap<String,Integer> wordMap, List<String> wordList){
        wordList.forEach(word -> {
            if(wordMap.containsKey(word)) wordMap.replace(word, wordMap.get(word)+1);
            else wordMap.put(word,1);
        });
    }

    /**
     * @param wordMap mappa parole-numeroOccorrenze da ordinare e tagliare
     * @param wordNumber numero di parole da prelevare
     * @return restituisce la lista delle parole più frequenti
     */
    private List<String[]> getOrderedResult(LinkedHashMap<String,Integer> wordMap, Integer wordNumber){
        LinkedHashMap<String,Integer> supportMap = wordMap;
        List<String[]> result = new ArrayList();

        supportMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(wordNumber).forEach(entry -> {
                    String[] newRow = {entry.getKey(), entry.getValue().toString()};
                    result.add(newRow);
                });

        return result;
    }

}

