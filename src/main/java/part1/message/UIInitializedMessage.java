package part1.message;

import part1.ui.GraphicUI;


public class UIInitializedMessage implements Message {
    private final GraphicUI ui;

    public UIInitializedMessage(GraphicUI ui) {
        this.ui = ui;
    }

    public GraphicUI getUI() {
        return ui;
    }


}
