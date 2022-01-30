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
}
