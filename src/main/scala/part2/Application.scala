package part2

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.ddata.GCounterKey
import akka.remote.RemoteTransportException
import com.typesafe.config.ConfigFactory

import part2.DataActorListener.Command

object Application {

  def main(args: Array[String]): Unit = {
    /* =============== actors initialization =============== */

    val ports =
      if (args.isEmpty)
        Seq(25251, 25252, 25253, 0)
      else
        args.toSeq.map(_.toInt)

    for (port <- ports) {
      try {
        // Override the configuration of the port
        val config = ConfigFactory.parseString(s"""
          akka.remote.artery.canonical.port=$port
          """).withFallback(ConfigFactory.load("application_cluster"))

        // Create an Akka system
        // ActorSystem[Nothing](RootClusterBehavior(), "ClusterSystem", config)
        var data: GCounterKey = null;
        var dataActorRef: ActorRef[Command] = null;
        ActorSystem[Nothing](Behaviors.setup[Nothing] { context =>
          // Create an actor that handles cluster domain events
          data = GCounterKey("counter")
          dataActorRef = context.spawn(DataActorListener(data), "DataListener")
          Behaviors.empty
        }, "ClusterSystem", config)

        /* =============== game initialization =============== */
        val n = 2
        val m = 2

        val imagePath = "src/main/resources/bletchley-park-mansion.jpg"

        val puzzle = new PuzzleBoard(n, m, imagePath, dataActorRef)
        puzzle.setVisible(true)

        return

      } catch {
        case _:RemoteTransportException => println("Port " + port + " already in use");
      }
    }
  }
}


object RootClusterBehavior {
  // Our root actor does nothing beside spawning our ClusterListener actor
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    // Create an actor that handles cluster domain events
    context.spawn(ClusterActorListener(), "ClusterListener")
    Behaviors.empty
  }
}

object RootDataBehavior {
  // Our root actor does nothing beside spawning our ClusterListener actor
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    // Create an actor that handles cluster domain events
    val data = GCounterKey("counter");
    val dataActorRef:ActorRef[Command] = context.spawn(DataActorListener(data), "DataListener")
    Behaviors.empty
  }
}