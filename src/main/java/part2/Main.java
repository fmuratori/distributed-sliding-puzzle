package part2;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import part2.message.Message;
import part2.view.PuzzleBoard;

import java.io.File;

public class Main {

    public static void main(final String[] args) {
        // configurations
        final int n = 2;
        final int m = 2;

        final String imagePath = "src/main/resources/bletchley-park-mansion.jpg";

        int[] ports = {25250,25251, 25252};
        for(int port: ports) {
            try {
                // actor creation
                Config config = ConfigFactory
                        .parseString("akka.remote.artery.canonical.port="+port)
                        .withFallback(ConfigFactory.parseFile(new File("src/main/resources/application_cluster.conf")));

                final ActorSystem<Message> system =
                        ActorSystem.create(Behaviors.empty(), "game", config);

                Cluster cluster = Cluster.get(system);
                cluster.manager().tell(Join.create(cluster.selfMember().address()));

                final PuzzleBoard puzzle = new PuzzleBoard(n, m, imagePath, system);
                puzzle.setVisible(true);

                System.out.println("Initialized cluster actor at port " + port);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
