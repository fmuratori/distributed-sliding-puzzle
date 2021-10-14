package part1.view;

import akka.actor.ActorRef;
import part1.message.FindFilesReqMessage;

public class SimpleView implements View {

    private String stopWordsFile;
    private String pdfFolder;

    public SimpleView(String stopWordsFile, String pdfFolder) {
        this.stopWordsFile = stopWordsFile;
        this.pdfFolder = pdfFolder;
    }

    @Override
    public void start(ActorRef actor) {

    }
}
