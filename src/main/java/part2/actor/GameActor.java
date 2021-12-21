package part2.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ddata.GCounter;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;
import part2.message.Message;
import part2.message.UIInitializedMessage;
import part2.message.UpdateMessage;

public class GameActor extends AbstractBehavior<Message> {

    private final ActorContext<Message> context;
    private ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter;
    private SelfUniqueAddress node;
    private GCounter counter;

    public GameActor(ActorContext<Message> context,
                     ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter) {
        super(context);

        this.replicatorAdapter = replicatorAdapter;
        this.context = context;

        // variabile mai usata ???
        final SelfUniqueAddress node = DistributedData.get(context.getSystem()).selfUniqueAddress();
        this.counter = GCounter.create();

        this.node = DistributedData.get(context.getSystem()).selfUniqueAddress();

//        this.replicatorAdapter.subscribe(this.key, DDataSubscribe::new);
    }


    public static Behavior<Message> create() {
        return Behaviors.setup(
            ctx ->
                DistributedData.withReplicatorMessageAdapter(
                    (ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter) ->
                        new GameActor(ctx, replicatorAdapter)));
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(UIInitializedMessage.class, this::onUIInitializedMessage)
//            .onMessage(DDSubscribeResponse.class, this::onDDSubscribeResponse)
            .build();
    }

    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        System.out.println("View ready");
        return this;
    }

    private Behavior<Message> onUpdateMessage(UpdateMessage message) {
        System.out.println("Update message");
        return this;
    }

//    private Behavior<Message> onDDSubscribeResponse(DDSubscribeResponse message) {
//        System.out.println("Distributed data message response");
//        return this;
//    }
}
