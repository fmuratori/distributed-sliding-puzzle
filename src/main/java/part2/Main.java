package part2;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.remote.RemoteTransportException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import part2.actor.GameActor;
import part2.message.Message;
import part2.view.PuzzleBoard;

import java.util.Optional;

public class Main {

    public static void main(final String[] args) {
        final int n = 2;
        final int m = 2;

        final String imagePath = "src/main/resources/bletchley-park-mansion.jpg";

//        int[] ports = {25252, 25253, 25254};
        int[] ports = {25252};

        for(int p : ports) {
            try {
                Config config = ConfigFactory
                        .parseString("akka.remote.artery.canonical.port=" + p)
                        .withFallback(ConfigFactory.load("application_cluster"));
                final ActorSystem<Message> system =
                        ActorSystem.create(Behaviors.empty(), "game", config);

                final PuzzleBoard puzzle = new PuzzleBoard(n, m, imagePath, system);
                puzzle.setVisible(true);

                System.out.println("Initialized cluster actor at port " + p);
                break;
            } catch (RemoteTransportException e) {
                System.out.println("Unable to initialize a distributed actor. Port " + p + " already occupied by another process.");
                // e.printStackTrace();
            }
        }
    }
}
