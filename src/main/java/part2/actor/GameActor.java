package part2.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ddata.GCounter;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;
import part2.message.Message;
import part2.message.UIInitializedMessage;

public class GameActor extends AbstractBehavior<Message> {

    private ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter;
    private SelfUniqueAddress node;
    private Key<GCounter> key;

    public GameActor(ActorContext<Message> context,
                     ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter,
                     Key<GCounter> key) {
        super(context);

        this.replicatorAdapter = replicatorAdapter;
        this.key = key;

        final SelfUniqueAddress node = DistributedData.get(context.getSystem()).selfUniqueAddress();

        this.node = DistributedData.get(context.getSystem()).selfUniqueAddress();

        // this.replicatorAdapter.subscribe(this.key, InternalSubscribeResponse::new);
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(UIInitializedMessage.class, this::onUIInitializedMessage)
                .build();
    }

    public static Behavior<Message> create(Key<GCounter> key) {
        return Behaviors.setup(
                ctx ->
                        DistributedData.withReplicatorMessageAdapter(
                                (ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter) ->
                                        new GameActor(ctx, replicatorAdapter, key)));
    }


    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        System.out.println("View ready");
        return this;
    }
}
