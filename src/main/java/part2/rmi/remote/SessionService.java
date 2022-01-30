package part2.rmi.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface SessionService extends Remote {

    String SERVICE_NAME = "SESSION";

    /*
        Un nuovo peer vuole connettersi
     */
    void connect(Integer port) throws RemoteException;

    /*
        Un peer vuole disconnettersi
     */
    void disconnect(Integer port) throws RemoteException;

    /*
        Un peer ancora non connesso richiede la lista di peers conosciuti
     */
    List<Integer> getPeers() throws RemoteException;


    /*
        Quando un peer richiede di fare una mossa e vuole entrare in CS
     */
    void receiveRequestAction(int port, Long timestamp) throws RemoteException;

    /*
        Quando un peer mi da il via libera ad effettuare una mossa
     */
    void receiveActionOK() throws RemoteException;

    /*
        Quando ricevo una mossa da eseguire da un peer
     */
    void receiveAction(List<Integer> newMap) throws RemoteException;
}
