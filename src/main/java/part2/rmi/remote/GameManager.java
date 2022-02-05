package part2.rmi.remote;

import part2.rmi.PuzzleBoard;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    private static final GameManager gameManager = new GameManager();

    private Map<Integer, Long> pendingRequests = new HashMap<>();
    private Long myTimestamp = -1L;
    private Integer numOK = 0;

    private PuzzleBoard board;

    private GameManager() { }

    public Long getMyTimestamp() {
        return myTimestamp;
    }

    public void setPuzzleBoard(PuzzleBoard board) {
        this.board = board;
    }

    public void newPuzzleBoard(List<Integer> newBoard) {
        this.board.updateBoard(newBoard);
    }

    public void requestAction() {
        System.out.println("Requesting CS...");
        pendingRequests = new HashMap<>();
        numOK = 0;
        myTimestamp = System.currentTimeMillis();
        if (ClientsManager.get().getConnections().isEmpty()) {
            this.increaseOKCount();
        } else {
            ClientsManager.get().getConnections().forEach((port, sessionService) -> {
                try {
                    sessionService.receiveRequestAction(Server.getInstance().getPort(), myTimestamp);
                } catch (RemoteException e) {
                    ClientsManager.get().deleteSessionService(port);
                    e.printStackTrace();
                }
            });
        }
    }

    public void addPendingRequest(int port, Long timestamp) {
        pendingRequests.put(port, timestamp);
    }

    public static GameManager get() {
        return gameManager;
    }

    public void increaseOKCount() {
        numOK++;
        if (numOK >= ClientsManager.get().getConnections().size()) {
            System.out.println("Entering CS...");

            // execute action in CS
            board.executeAction();

            // sending new map configuration to each peer
            ClientsManager.get().getConnections().forEach((port, session) -> {
                try {
                    System.out.println("Sending new map configuration to peers...");
                    session.receiveAction(board.getMap());
                } catch (RemoteException e) {
                    System.out.println("Unable to send map configuration to peers ...");
                    e.printStackTrace();
                }
            });

            // release CS
            System.out.println("Exiting CS...");
            myTimestamp = -1L;
            pendingRequests.forEach((port, timestamp) -> {
                try {
                    ClientsManager.get().getConnection(port).receiveAction(board.getMap());
                    ClientsManager.get().getConnection(port).receiveActionOK();
                } catch (RemoteException e) {
                    ClientsManager.get().deleteSessionService(port);
                    e.printStackTrace();
                }
            });
            pendingRequests = new HashMap<>();
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
