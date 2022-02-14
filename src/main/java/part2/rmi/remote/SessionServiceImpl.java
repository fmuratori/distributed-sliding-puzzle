package part2.rmi.remote;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * Controlla se vale la relazione di happened before tra 2 vector clock:
     * true se tutti i valori di firstVector sono <= rispetto a secondVector ed almeno un valore in firstVector è <
     * al corrispondente in secondVector, false altrimenti.
     */
    private boolean checkHappenedBefore(Map<Integer, Integer> firstVector, Map<Integer, Integer> secondVector) {

        boolean allLowerOrEqual = true;
        boolean atLeastOneIsLower = false;

        for (Map.Entry<Integer, Integer> entry : firstVector.entrySet()) {
            boolean isGreater = entry.getValue() > secondVector.get(entry.getKey());
            boolean isLower = entry.getValue() < secondVector.get(entry.getKey());
            if (isGreater) {
                allLowerOrEqual = false;
                break;
            }
            if (isLower) {
                atLeastOneIsLower = true;
            }
        }
        return allLowerOrEqual && atLeastOneIsLower;
    }

    private void updateVectorClock(Map<Integer, Integer> vectorClock) {
        ClientsManager.get().getVectorClock().forEach((port2, lClock) -> {
            if (port2.equals(Server.getInstance().getPort())) {
                ClientsManager.get().getVectorClock().put(port2, lClock+1);
            } else {
                ClientsManager.get().getVectorClock().put(port2, Math.max(vectorClock.get(port2), lClock));
            }
        });
    }

    @Override
    public void receiveRequestAction(int port, Map<Integer, Integer> vectorClock) throws RemoteException {
        this.updateVectorClock(vectorClock);
        if (GameManager.get().getTimestamp().isEmpty())
            System.out.println("Requested CS for an action. RECEIVED VECTOR CLOCK: " + vectorClock.toString());
        else
            System.out.println("Requested CS for an action. My VECTOR CLOCK:" +
                    GameManager.get().getTimestamp().toString() + "RECEIVED VECTOR CLOCK: " + vectorClock.toString());

        if (GameManager.get().getTimestamp().isEmpty() ||
                this.checkHappenedBefore(vectorClock, GameManager.get().getTimestamp())) {
            System.out.println("Allowing action.");
            ClientsManager.get().getConnection(port).receiveActionOK();
        } else {
            System.out.println("Not allowing action. Adding request to pending list.");
            GameManager.get().addPendingRequest(port);
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
