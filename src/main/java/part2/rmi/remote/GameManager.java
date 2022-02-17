package part2.rmi.remote;

import part2.rmi.PuzzleBoard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameManager {
    private static final GameManager gameManager = new GameManager();

    private List<Integer> pendingRequests = new ArrayList<>();
    private Map<Integer, Boolean> receivedACK = new HashMap<>();
    private Map<Integer, Integer> requestVClock = new HashMap<>();

    private PuzzleBoard board;

    private final Runnable timeExpired = () -> {
        System.out.println("Started waiting for ACK");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Integer> expired = receivedACK
                .entrySet()
                .stream()
                .filter(e -> !e.getValue())
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (expired.size() > 0) {
            System.out.println("ACK wait expired for peers at port" + expired.toString());
            expired.forEach(port -> {
                ClientsManager.get().deletePeer(port);
                receivedACK.remove(port);
                requestVClock.remove(port);
                pendingRequests.remove(port);
            });

            registerACK(-1);
        }
    };

    private GameManager() { }

    public void setPuzzleBoard(PuzzleBoard board) {
        this.board = board;
    }

    public void newPuzzleBoard(List<Integer> newBoard) {
        this.board.updateBoard(newBoard);
    }

    public void requestAction() {
        System.out.println("Requesting CS...");
        if (ClientsManager.get().getConnections().size() == 0) {
            registerACK(-1);
        } else {
            pendingRequests = new ArrayList<>();
            receivedACK = new HashMap<>();
            ClientsManager.get().getConnections().forEach((port, sessionService) -> receivedACK.put(port, false));
            new Thread(timeExpired).start();

            // increasing my logic clock
            Map<Integer, Integer> vClock = ClientsManager.get().getVectorClock();
            vClock.put(Server.getInstance().getPort(), vClock.get(Server.getInstance().getPort()) + 1);
            requestVClock = new HashMap<>(vClock);
            // sending messages
            ClientsManager.get().getConnections().forEach((port, sessionService) -> {
                receivedACK.put(port, false);
                try {
                    sessionService.receiveRequestAction(Server.getInstance().getPort(), ClientsManager.get().getVectorClock());
                } catch (RemoteException e) {
                    ClientsManager.get().deletePeer(port);
                    e.printStackTrace();
                }
            });
        }
    }

    public void addPendingRequest(int port) {
        pendingRequests.add(port);
    }

    public Map<Integer, Integer> getTimestamp() {
        return requestVClock;
    }

    public static GameManager get() {
        return gameManager;
    }

    public void registerACK(int port) {
        if (port != -1) {
            receivedACK.put(port, true);
        }
        if (receivedACK.values().stream().allMatch(p -> p)) {
            System.out.println("Entering CS...");

//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            // execute action in CS
            board.executeAction();

            // sending new map configuration to each peer
            ClientsManager.get().getConnections().forEach((peerPort, session) -> {
                try {
                    System.out.println("Sending new map configuration to peers...");
                    session.receiveAction(board.getMap());
                } catch (RemoteException e) {
                    System.out.println("Unable to send map configuration to peers ...");
                    e.printStackTrace();
                }
            });

            requestVClock = new HashMap<>();

            // release CS
            System.out.println("Exiting CS...");
            pendingRequests.forEach((peerPort) -> {
                try {

                    ClientsManager.get().getConnection(peerPort).receiveACK(Server.getInstance().getPort());
                } catch (RemoteException e) {
                    ClientsManager.get().deletePeer(peerPort);
                    e.printStackTrace();
                }
            });
            pendingRequests = new ArrayList<>();
        }
    }

    public void sendCurrentMap(Integer port) {
        try {
            System.out.println(ClientsManager.get().getConnections().toString());

            ClientsManager.get().getConnection(port).receiveAction(board.getMap());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
