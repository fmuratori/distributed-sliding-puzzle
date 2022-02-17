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
    private final SessionServiceImpl sessionService;

    private Server(int port) throws RemoteException {

        System.out.println("Server is starting...");

        this.port = port;
        registry = LocateRegistry.createRegistry(port);
        System.out.println("RMI registry is running on port " + port);

        System.out.println("Binding SessionService...");
        sessionService = new SessionServiceImpl();
        registry.rebind(SessionService.SERVICE_NAME, sessionService);

        System.out.println("SessionService is ready.");
    }

    public void terminate() throws RemoteException, NotBoundException {

        System.out.println("Shutting down the RMI registry...");

        registry.unbind(SessionService.SERVICE_NAME);
        UnicastRemoteObject.unexportObject(sessionService, true);
        UnicastRemoteObject.unexportObject(registry, true);

        System.out.println("Server has stopped.");
    }

    public SessionService getSession() {
        return sessionService;
    }

    public Integer getPort() {
        return port;
    }

    public static Server get() {
        return server;
    }

    // simple static factory utilities
    public static void initialize(int port) throws RemoteException {
        server = new Server(port);
    }
}
