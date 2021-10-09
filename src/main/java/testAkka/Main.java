package testAkka;

import akka.actor.typed.ActorSystem;
import part1.FileReader;
import part1.messages.JobMessage;
import part1.messages.JobRequestMessage;

import java.io.File;

public class Main {
  public static void main(String[] args) throws Exception {

	  File folder = new File(".");
	  System.out.println(folder.getPath());

/*
	  final ActorSystem<HelloMsg> system =
			    ActorSystem.create(HappyActorBehaviour.create(), "hello");

	  system.tell(new HelloMsg("Hello"));
	  system.tell(new HelloMsg("World"));
*/
  }
}
