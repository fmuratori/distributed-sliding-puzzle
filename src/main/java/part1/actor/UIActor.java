package part1.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import part1.message.*;

import javax.swing.*;
import java.util.Calendar;
import java.util.List;

public class UIActor extends AbstractBehavior<Message> {

    private JTextArea textArea;
    private JButton startButton, stopButton;

    private final ActorRef<Message> taskActor;
    private Integer processedFiles = 0;
    private boolean stopped = false;

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
                .onMessage(UIInitializedMessage.class, this::onUIInitializedMessage)
                .onMessage(StartTaskReqMessage.class, this::onStartTaskReqMessage)
                .onMessage(TaskUpdateMessage.class, this::onTaskUpdateMessage)
                .onMessage(StopTaskMessage.class, this::onStopTaskMessage)
                .build();
    }

    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        this.textArea = message.getTextArea();
        this.startButton = message.getStartButton();
        this.stopButton = message.getStopButton();

        return this;
    }

    private Behavior<Message> onStartTaskReqMessage(StartTaskReqMessage message) {
        timeStart = Calendar.getInstance();

        this.processedFiles = 0;
        this.stopped = false;

        taskActor.tell(message);
        return this;
    }

    private Behavior<Message> onStopTaskMessage(StopTaskMessage message){
        this.stopped = true;
        return this;
    }

    private Behavior<Message> onTaskUpdateMessage(TaskUpdateMessage message) {

        this.processedFiles += 1;
        if(processedFiles == message.getTotalFileNumber()) {
            this.timeEnd = Calendar.getInstance();
            System.out.println("TEMPO DI ESECUZIONE TASK: " + (timeEnd.getTimeInMillis() - timeStart.getTimeInMillis()));
        }

        if(this.processedFiles == message.getTotalFileNumber()){
            this.startButton.setEnabled(true);
            this.stopButton.setEnabled(false);
        }

        if(!stopped){

            this.textArea.setText(message.getTotalWordsCount().toString());

            //update textarea of the ui
            this.textArea.setText(getResultText(
                    message.getWordList(),
                    message.getTotalWordsCount(),
                    message.getTotalFileNumber())
            );

        }

        return this;
    }

    /**
     * @param wordList
     * @param totalWordsCount
     * @return return the text block to be rendered in the textarea
     */
    private String getResultText(List<String[]> wordList, Integer totalWordsCount, Integer totalFileNumber){

        String textBlock = "Pdf analizzati: " + this.processedFiles + "/" + totalFileNumber + "\n" +
                "Numero di parole totali: " + totalWordsCount + "\n";

        for(String[] word: wordList){
            textBlock += "PAROLA: " + word[0] + ", OCCORRENZE: " + word[1] + "\n";
        };

        return textBlock;
    }

}
