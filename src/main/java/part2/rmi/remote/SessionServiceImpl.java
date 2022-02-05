package part2.rmi.remote;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SessionServiceImpl extends UnicastRemoteObject implements SessionService  {

    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Integer> peers;

    protected SessionServiceImpl() throws RemoteException {
        super();
        peers = new ArrayList<>();
    }

    // ################################# CLIENTS CONNECTION #################################

    @Override
    public void connect(Integer port) throws RemoteException {
        System.out.println("Received connection request from peer at port " + port + "...");
        boolean result = ClientsManager.get().receiveNewConnection(port);
        if (!result) {
            System.out.println("Unable to retrieve Java RMI remote registry from peer at port " + port);
            throw new RemoteException();
        } else {
            peers.add(port);
            System.out.println("Peer at port " + port + " connected successfully. PEERS: " + peers.toString());
        }
    }

    @Override
    public void disconnect(Integer port) throws RemoteException {
        System.out.println("Received disconnection request from peer at port " + port + "...");
        peers.remove(port);
        ClientsManager.get().deleteSessionService(port);
        System.out.println("Peer at port " + port + " disconnected successfully. PEERS: " + peers.toString());
    }

    @Override
    public List<Integer> getPeers() throws RemoteException {
        System.out.println("Requested currently connected peers. PEERS: " + peers.toString());
        return peers;
    }

    // ################################# GAME STATE AND CS #################################

    @Override
    public void receiveRequestAction(int port, Long timestamp) throws RemoteException {
        System.out.println("Requested CS for an action.");
        if (GameManager.get().getMyTimestamp() == -1 || timestamp < GameManager.get().getMyTimestamp()) {
            System.out.println("Allowing action.");
            ClientsManager.get().getConnection(port).receiveActionOK();
        } else {
            System.out.println("Not allowing action. Adding request to pending list.");
            GameManager.get().addPendingRequest(port, timestamp);
        }
    }

    @Override
    public void receiveActionOK() throws RemoteException {
        System.out.println("Received action OK from a peer.");
        GameManager.get().increaseOKCount();
    }

    @Override
    public void receiveAction(List<Integer> newMap) throws RemoteException {
        System.out.println("Received new game configuration from a peer.");
        GameManager.get().newPuzzleBoard(newMap);
    }

    @Override
    public void receiveMapRequest(int port) throws RemoteException {
        System.out.println("Received map request. Sending current map to the caller at port " + port + "...");
        GameManager.get().sendCurrentMap(port);
    }
}
