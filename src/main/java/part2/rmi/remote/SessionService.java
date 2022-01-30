package part2.rmi.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface SessionService extends Remote {

    String SERVICE_NAME = "SESSION";

    void connect(Integer port) throws RemoteException;

    void disconnect(Integer port) throws RemoteException;

    List<Integer> getPeers() throws RemoteException;
}
