package part1;

import akka.actor.typed.ActorSystem;
import part1.messages.JobMessage;
import part1.messages.JobRequestMessage;

import java.util.ArrayList;

public class MainPart1 {
    public static void main(String[] args) {

        FileReader fr = new FileReader();
        ArrayList<String> fileList = fr.getFilesInFolder("assets/pdf", "pdf");
        System.out.println("SIZE: " + fileList.size());

        /*
        TODO: parallelizzare l'operazione, adesso sembra che faccia tutto in maniera sequenziale
         */
        final ActorSystem<JobMessage> system =
                ActorSystem.create(WorkerActorBehavior.create(), "hello");

        for (String filePath : fileList)
            system.tell(new JobRequestMessage(filePath));
    }
}
