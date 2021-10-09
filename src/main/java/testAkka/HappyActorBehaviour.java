package testAkka;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class HappyActorBehaviour extends AbstractBehavior<HelloMsg> {

	public static Behavior<HelloMsg> create() {
		return Behaviors.setup(HappyActorBehaviour::new);
	}

	private HappyActorBehaviour(ActorContext<HelloMsg> context) {
		super(context);
	}

	@Override
	public Receive<HelloMsg> createReceive() {
		return newReceiveBuilder().onMessage(HelloMsg.class, this::onHelloMsg).build();
	}

	private Behavior<HelloMsg> onHelloMsg(HelloMsg msg) {
		System.out.println("Hello " + msg.getContent());
		return this;
	}

}
