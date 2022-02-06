package part1.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.message.*;
import part1.ui.GraphicUI;

import java.util.Calendar;
import java.util.List;

public class UIActor extends AbstractBehavior<Message> {

    private GraphicUI ui;
    private final ActorRef<Message> taskActor;
    private Integer processedFiles = 0;

    //servono per calcolare il tempo di esecuzione
    Calendar timeStart, timeEnd;

    public UIActor(ActorContext<Message> context) {
        super(context);
            taskActor = getContext().spawnAnonymous(TaskActor.create());
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(UIActor::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(TerminateSystemMessage.class, this::onTerminateSystemMessage)
                .onMessage(UIInitializedMessage.class, this::onUIInitializedMessage)
                .onMessage(StartTaskReqMessage.class, this::onStartTaskReqMessage)
                .onMessage(StopTaskMessage.class, this::onStopTaskMessage)
                .onMessage(TaskUpdateMessage.class, this::onTaskUpdateMessage)
                .build();
    }

    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        this.ui = message.getUI();
        return this;
    }

    private Behavior<Message> onStartTaskReqMessage(StartTaskReqMessage message) {
        timeStart = Calendar.getInstance();

        taskActor.tell(message);
        return this;
    }

    private Behavior<Message> onStopTaskMessage(StopTaskMessage message) {
        taskActor.tell(message);
        return this;
    }

    private Behavior<Message> onTerminateSystemMessage(TerminateSystemMessage message) {
        getContext().getSystem().terminate();
        return this;
    }

    private Behavior<Message> onTaskUpdateMessage(TaskUpdateMessage message) {

        processedFiles += 1;
        if(processedFiles.equals(message.getTotalFileNumber())) {
            timeEnd = Calendar.getInstance();
            System.out.println("TEMPO DI ESECUZIONE TASK: " + (timeEnd.getTimeInMillis() - timeStart.getTimeInMillis()));

            ui.startButton.setEnabled(true);
            ui.stopButton.setEnabled(false);
        }

        ui.resultConsole.setText(message.getTotalWordsCount().toString());

        //update textarea of the ui
        ui.resultConsole.setText(getResultText(
                message.getWordList(),
                message.getTotalWordsCount(),
                message.getTotalFileNumber())
        );
        return this;
    }

    /**
     * @param wordList
     * @param totalWordsCount
     * @return return the text block to be rendered in the textarea
     */
    private String getResultText(List<String[]> wordList, Integer totalWordsCount, Integer totalFileNumber){

        StringBuilder textBlock = new StringBuilder("Pdf analizzati: " + processedFiles + "/" + totalFileNumber + "\n" +
                "Numero di parole totali: " + totalWordsCount + "\n");

        for(String[] word: wordList){
            textBlock.append("PAROLA: ").append(word[0]).append(", OCCORRENZE: ").append(word[1]).append("\n");
        };

        return textBlock.toString();
    }

}
