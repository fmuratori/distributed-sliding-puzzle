package part1;

import akka.actor.typed.ActorSystem;
import part1.actor.*;
import part1.message.Message;
import part1.ui.GraphicUI;

public class Main {

    public static void main(String[] args) {
        final ActorSystem<Message> system =
                ActorSystem.create(UIActor.create(), "task");

        GraphicUI v = new GraphicUI(system);

//        String defaultFileChooserText = "assets/stopwords.pdf";
//        String defaultFolderChooserText = "assets/pdf";
//
//        final ActorSystem<Message> system = ActorSystem.create(TaskActor.create(), "task");
//        system.tell(new StartTaskReqMessage(defaultFolderChooserText, defaultFileChooserText, 10, system));

    }

}
