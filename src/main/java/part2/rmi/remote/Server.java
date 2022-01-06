package part2.rmi.remote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
    Classe singleton dedicata alla raccolta di tutte le funzionalità Java RMI orientate alla fornitura di funzionalità e strutture
    per processi peer
 */
public class Server {
    private static Server server;

    private final int port;
    private final Registry registry;
    private final ServerServiceImpl service;

    private Server(int port) throws RemoteException {
        this.port = port;
        registry = LocateRegistry.createRegistry(1099);
        service = new ServerServiceImpl();
        registry.rebind(ServerService.SERVICE_NAME, service);
    }

    public void stopServer() throws RemoteException, NotBoundException {
        registry.unbind(ServerService.SERVICE_NAME);
        UnicastRemoteObject.unexportObject(service, true);
        UnicastRemoteObject.unexportObject(registry, true);
    }

    public static Server getInstance() {
        return server;
    }

    // simple static factory utilities
    public static void initialize(int port) {
        try {
            server = new Server(port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public static void initialize() {
        initialize(1099);
    }
}
