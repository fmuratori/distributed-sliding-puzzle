package part2.rmi.remote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestClientConnection {
    public static void main(String[] args) {
        List<Integer> sockets = new ArrayList<>(Arrays.asList(12346, 12347));

        for (Integer port : sockets) {
            try {
                Server.initialize(port);

                ClientsManager.get().connect(12345);

                Thread.sleep(10000);

                ClientsManager.get().disconnect();

                Server.get().terminate();

                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Port " + port + " already in use.");
            }
        }
    }
}
