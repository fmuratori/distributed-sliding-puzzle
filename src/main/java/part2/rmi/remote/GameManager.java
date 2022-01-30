package part2.rmi.remote;

import part2.rmi.PuzzleBoard;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    private static GameManager gameManager = new GameManager();

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
        pendingRequests = new HashMap<>();
        numOK = 0;
        myTimestamp = System.currentTimeMillis();
        ClientsManager.get().getConnections().forEach((port, sessionService) -> {
            try {
                sessionService.receiveRequestAction(Server.getInstance().getPort(), myTimestamp);
            } catch (RemoteException e) {
                ClientsManager.get().deleteSessionService(port);
                e.printStackTrace();
            }
        });
    }

    public void addPendingRequest(int port, Long timestamp) {
        pendingRequests.put(port, timestamp);
    }

    public static GameManager get() {
        return gameManager;
    }

    public void increaseOKCount() {
        numOK++;
        if (numOK == ClientsManager.get().getConnections().size()) {
            // execute action in CS
            board.executeAction();

            // release CS
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
}
