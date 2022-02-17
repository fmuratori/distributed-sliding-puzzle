package part2.rmi.remote;

import java.util.*;

public class TestSession {
    public static void main(String[] args) {
        List<Integer> sockets = new ArrayList<>(Arrays.asList(12345, 12346, 12347));

        for (Integer port : sockets) {
            try {
                Server.initialize(port);

                if (port != 12345) {
                    ClientsManager.get().connect(12345);
                }

                Thread.sleep(50000);

                ClientsManager.get().disconnect();

                Thread.sleep(100000);

                Server.get().terminate();

                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Port " + port + " already in use.");
            }
        }
    }
}
