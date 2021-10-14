package part1.message;

import javax.swing.*;

public class UIInitializedMessage implements Message {
    public JTextArea textArea;

    public UIInitializedMessage(JTextArea textArea) {
        this.textArea = textArea;
    }
}
