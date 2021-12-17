package part2;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import part2.actor.GameActor;
import part2.message.Message;
import part2.view.PuzzleBoard;

public class Main {

    public static void main(final String[] args) {
        final int n = 3;
        final int m = 5;

        final String imagePath = "src/main/resources/bletchley-park-mansion.jpg";

        // load akka clustering configuration
        int port = 25252;
        Config config = ConfigFactory
                .parseString("akka.remote.artery.canonical.port="+port)
                .withFallback(ConfigFactory.load("application_cluster"));

        final ActorSystem<Message> system =
                ActorSystem.create(Behaviors.empty(), "game", config);

        final PuzzleBoard puzzle = new PuzzleBoard(n, m, imagePath, system);
        puzzle.setVisible(true);

    }

}
