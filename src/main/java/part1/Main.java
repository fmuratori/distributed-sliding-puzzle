package part1;

import akka.actor.typed.ActorSystem;
import part1.actor.*;
import part1.message.Message;
import part1.message.StartTaskMessage;

public class Main {
    private static final String folder = "C:\\Users\\Fabio\\Desktop\\pcd_workspace\\pcd-assignment-03\\data";
    private static final String stopWordsFile = "C:\\Users\\Fabio\\Desktop\\pcd_workspace\\pcd-assignment-03\\data\\stopwords.txt";

    public static void main(String[] args) {

        final ActorSystem<Message> system =
                ActorSystem.create(TaskActor.create(), "task");

        system.tell(new StartTaskMessage(folder, stopWordsFile));
    }
}
