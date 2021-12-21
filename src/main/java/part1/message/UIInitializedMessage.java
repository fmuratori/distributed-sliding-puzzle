package part1.message;

import javax.swing.*;

public class UIInitializedMessage implements Message {
    private JTextArea textArea;
    private JButton startButton, stopButton;

    public UIInitializedMessage(JTextArea textArea, JButton startButton, JButton stopButton) {
        this.textArea = textArea;
        this.startButton = startButton;
        this.stopButton = stopButton;
    }

    public JTextArea getTextArea() {
        return this.textArea;
    }

    public  JButton getStartButton() { return this.startButton; }

    public  JButton getStopButton() { return this.stopButton; }

}