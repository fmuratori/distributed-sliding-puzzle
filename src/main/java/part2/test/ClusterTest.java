package part2.test;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.ClusterEvent;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.typed.*;
import part1.actor.UIActor;
import part1.message.Message;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

public class ClusterTest {
    public static void main(String[] args) {
        // load akka clustering configuration
        int port = 25252;
        Config config = ConfigFactory
                .parseString("akka.remote.artery.canonical.port="+port)
                .withFallback(ConfigFactory.load("application_cluster"));

        final ActorSystem<Object> system =
                ActorSystem.create(Behaviors.empty(), "game", config);

        // akka distributed data entry point
        final SelfUniqueAddress node = DistributedData.get(system).selfUniqueAddress();

        // define and use the cluster
        Cluster cluster = Cluster.get(system);

        cluster.manager().tell(Join.create(cluster.selfMember().address()));

        System.out.println(cluster.selfMember());

//        cluster.subscriptions().tell(Subscribe.create(subscriber, ClusterEvent.MemberEvent.class));

        cluster.manager().tell(Leave.create(cluster.selfMember().address()));
    }
}
