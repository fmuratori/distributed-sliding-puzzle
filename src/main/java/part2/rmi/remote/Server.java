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
    private final SessionServiceImpl service;

    private Server(int port) throws RemoteException {

        System.out.println("Server is starting...");

        this.port = port;
        registry = LocateRegistry.createRegistry(port);
        System.out.println("RMI registry is running on port " + port);

        System.out.println("Binding SessionService...");
        service = new SessionServiceImpl();
        registry.rebind(SessionService.SERVICE_NAME, service);

        System.out.println("SessionService is ready.");
    }

    public void terminate() throws RemoteException, NotBoundException {

        System.out.println("Shutting down the RMI registry...");

        registry.unbind(SessionService.SERVICE_NAME);
        UnicastRemoteObject.unexportObject(service, true);
        UnicastRemoteObject.unexportObject(registry, true);

        System.out.println("Server has stopped.");
    }

    public SessionService getSession() {
        return service;
    }

    public Integer getPort() {
        return port;
    }

    public static Server getInstance() {
        return server;
    }

    // simple static factory utilities
    public static void initialize(int port) throws RemoteException {
        server = new Server(port);
    }
}
