package part2.rmi.remote;

import java.util.ArrayList;
import java.util.List;

/*
    Classe dedicata alla raccolta di tutte le funzionalità Java RMI orientate a richieste effettuate verso altri
    processi peer
 */
public class ClientsManager {
    private static ClientsManager clientsManager;
    private List<Client> clients;

    private ClientsManager() {
        clients = new ArrayList<>();
    }

    public void addClient(int ip, int port) {
        this.clients.add(new Client(ip, port));
    }

    public static ClientsManager getInstance() {
        return clientsManager;
    }
}
