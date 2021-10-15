package part1.message;

import javax.swing.*;

public class UIInitializedMessage implements Message {
    private JTextArea textArea;

    public UIInitializedMessage(JTextArea textArea) {
        this.textArea = textArea;
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
