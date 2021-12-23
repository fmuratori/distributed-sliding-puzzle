package part2.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ddata.GCounter;
import akka.cluster.ddata.GCounterKey;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.ddata.typed.javadsl.Replicator;
import akka.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;
import part2.message.Message;
import part2.message.UIInitializedMessage;

public class GameActor extends AbstractBehavior<Message> {

    public static class Increment implements Message {
        public Increment(){}
    }

    public static class Unsubscribe  implements Message {
        private Unsubscribe(){}
    }

    public static class GetValue implements Message {
        public final ActorRef<Integer> replyTo;

        public GetValue(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class GetCachedValue implements Message    {
        public final ActorRef<Integer> replyTo;

        public GetCachedValue(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    private static class InternalUpdateResponse implements Message {
        final Replicator.UpdateResponse<GCounter> rsp;

        InternalUpdateResponse(Replicator.UpdateResponse<GCounter> rsp) {
            this.rsp = rsp;
        }
    }

    private static class InternalGetResponse implements Message {
        final Replicator.GetResponse<GCounter> rsp;
        final ActorRef<Integer> replyTo;

        InternalGetResponse(Replicator.GetResponse<GCounter> rsp, ActorRef<Integer> replyTo) {
            this.rsp = rsp;
            this.replyTo = replyTo;
        }
    }

    private static final class InternalSubscribeResponse implements Message {
        final Replicator.SubscribeResponse<GCounter> rsp;

        InternalSubscribeResponse(Replicator.SubscribeResponse<GCounter> rsp) {
            this.rsp = rsp;
        }
    }

    private final ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter;
    private final SelfUniqueAddress node;
    private final Key<GCounter> counter;
    private int cachedValue = 0;

    private GameActor(ActorContext<Message> context,
                     ReplicatorMessageAdapter<Message, GCounter> replicatorAdapter) {
        super(context);

        this.replicatorAdapter = replicatorAdapter;

        this.node = DistributedData.get(context.getSystem()).selfUniqueAddress();
        this.counter = new GCounterKey("counter");

        this.replicatorAdapter.subscribe(this.counter, InternalSubscribeResponse::new);
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
            .onMessage(Increment.class, this::onIncrement)
            .onMessage(InternalUpdateResponse.class, msg -> {
                System.out.println("InternalUpdateResponse");
                return Behaviors.same();
            })
            .onMessage(GetValue.class, this::onGetValue)
            .onMessage(GetCachedValue.class, this::onGetCachedValue)
            .onMessage(Unsubscribe.class, this::onUnsubscribe)
            .onMessage(InternalGetResponse.class, this::onInternalGetResponse)
            .onMessage(InternalSubscribeResponse.class, this::onInternalSubscribeResponse)
            .build();
    }

    private Behavior<Message> onIncrement(Increment cmd) {
        replicatorAdapter.askUpdate(
                askReplyTo ->
                        new Replicator.Update<>(
                                counter,
                                GCounter.empty(),
                                Replicator.writeLocal(),
                                askReplyTo,
                                curr -> {
                                    System.out.println("onIncrement " + curr);
                                    return curr.increment(this.node, 1);
                                }),
                InternalUpdateResponse::new);

        return this;
    }

    private Behavior<Message> onGetValue(GetValue cmd) {
        replicatorAdapter.askGet(
                askReplyTo -> new Replicator.Get<>(counter, Replicator.readLocal(), askReplyTo),
                rsp -> new InternalGetResponse(rsp, cmd.replyTo));

        return this;
    }

    private Behavior<Message> onGetCachedValue(GetCachedValue cmd) {
        cmd.replyTo.tell(cachedValue);
        return this;
    }

    private Behavior<Message> onUnsubscribe(Unsubscribe cmd) {
        replicatorAdapter.unsubscribe(counter);
        return this;
    }

    private Behavior<Message> onInternalGetResponse(InternalGetResponse msg) {
        if (msg.rsp instanceof Replicator.GetSuccess) {
            int value = ((Replicator.GetSuccess<?>) msg.rsp).get(this.counter).getValue().intValue();
            msg.replyTo.tell(value);
            return this;
        } else {
            return Behaviors.unhandled();
        }
    }

    private Behavior<Message> onInternalSubscribeResponse(InternalSubscribeResponse msg) {
        if (msg.rsp instanceof Replicator.Changed) {
            GCounter counter = ((Replicator.Changed<?>) msg.rsp).get(this.counter);
            cachedValue = counter.getValue().intValue();
            System.out.println("onInternalSubscribeResponse " + cachedValue);
            return this;
        } else {
            return Behaviors.unhandled();
        }
    }

    private Behavior<Message> onUIInitializedMessage(UIInitializedMessage message) {
        System.out.println("View ready");
        return this;
    }
}
