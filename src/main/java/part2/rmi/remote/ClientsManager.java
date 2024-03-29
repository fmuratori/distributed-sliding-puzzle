package part2.rmi.remote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/*
    Classe dedicata alla raccolta di tutte le funzionalità Java RMI orientate a richieste effettuate verso altri
    processi peer
 */
public class ClientsManager {

    private static ClientsManager instance = null;

    private final Map<Integer, SessionService> peersSessionServices;
    private final Map<Integer, Integer> vectorClock;

    private ClientsManager() {
        peersSessionServices = new HashMap<>();
        vectorClock = new HashMap<>();
        vectorClock.put(Server.get().getPort(), 0);
    }

    public void connect(Integer peerPort) {
        System.out.println("Starting session by connecting to peer at port " + peerPort + "...");
        this.connect(peerPort, true);
    }

    private void connect(Integer peerPort, boolean isFirstConnection) {
        this.accessRemoteRegistry(peerPort).ifPresent(sessionService -> {
                if (isFirstConnection) {
                    try {
                        // initially we ask a peer to get his own connected peers (a list of ports)
                        System.out.println("Attempting to get peers connected to the peer at port " + peerPort + "...");
                        List<Integer> peers = sessionService.getPeers();
                        System.out.println("Found peers: " + peers.toString());

                        // a connection is attempted for each peer reachable by the starting peer
                        peers.forEach(port -> connect(port, false));

                    } catch (RemoteException e) {
                        System.out.println("Unable to connect to the peer at port " + peerPort);
                        e.printStackTrace();
                    }
                }

                try {
                    // "send" a connection request to the peer
                    System.out.println("Sending connection request to peer at port " + peerPort + "...");
                    sessionService.connect(Server.get().getPort());


                    System.out.println("Requesting the map configuration to the peer at port " + peerPort + "...");
                    sessionService.receiveMapRequest(Server.get().getPort());

                    peersSessionServices.put(peerPort, sessionService);
                    vectorClock.put(peerPort, 0);
                    System.out.println("Connected to peer at port " + peerPort);

                } catch (RemoteException e) {
                    System.out.println("Unable to connect to the peer at port " + peerPort);
                    e.printStackTrace();
                }
            });

    }

    public boolean receiveNewConnection(int port) {
        Optional<SessionService> sessionService = this.accessRemoteRegistry(port);
        sessionService.ifPresent(service -> {
            peersSessionServices.put(port, service);
            vectorClock.put(port, 0);
            System.out.println("NEW VCLOCK: " + vectorClock.toString());
        });
        return sessionService.isPresent();
    }

    public Map<Integer, Integer> getVectorClock() {
        return vectorClock;
    }

    public void disconnect() {
        System.out.println("Closing session by disconnecting from every peer...");

        peersSessionServices.forEach((port, sessionService) -> {
            try {
                System.out.println("Disconnecting from peer at port " + port + "...");
                peersSessionServices.remove(port);
                vectorClock.remove(port);
                System.out.println("Disconnected from peer at port " + port);
                sessionService.disconnect(Server.get().getPort());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        });
        
        System.out.println("Session closed");
    }

    public void deletePeer(Integer port) {
        vectorClock.remove(port);
        peersSessionServices.remove(port);
    }

    private Optional<SessionService> accessRemoteRegistry(Integer port) {

        try {
            Registry registry = LocateRegistry.getRegistry(port);
            return Optional.of((SessionService) registry.lookup(SessionService.SERVICE_NAME));
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Peer at port " + port + " is unreachable");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Map<Integer, SessionService> getConnections() {
        return peersSessionServices;
    }

    public SessionService getConnection(int port) {
        return peersSessionServices.get(port);
    }

    public static ClientsManager get() {
        if (instance == null)
            instance = new ClientsManager();
        return instance;
    }

}
