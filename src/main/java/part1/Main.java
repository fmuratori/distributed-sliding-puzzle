package part1;

import akka.actor.typed.ActorSystem;
import part1.actor.*;
import part1.message.Message;
import part1.ui.GraphicUI;

public class Main {

    /**
     * BENCHMARKS
     *
     * 1 worker - 53.02
     * 2 worker - 32.58
     * 3 worker - 28.52
     * 4 worker - 27.44 ,26.82, 26.17
     * 5 worker - 24.88, 24.73,s 25.12
     * 6 worker - 25.24, 24.23, 24.98
     * 7 worker - 25.66, 25.2, 25.82
     * 8 worker - 26.70, 27.08, 26.24
     * 9 worker - 26.05, 26.37, 26.03
     * 10 worker - 26.27, 27.06, 26.92
     */

    public static void main(String[] args) {
        final ActorSystem<Message> system =
                ActorSystem.create(UIActor.create(), "task");

        new GraphicUI(system);
    }

}
