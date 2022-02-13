package part2.rmi.remote;

import part2.rmi.PuzzleBoard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameManager {
    private static final GameManager gameManager = new GameManager();

    private List<Integer> pendingRequests = new ArrayList<>();
    private Integer numOK = 0;

    private PuzzleBoard board;

    private GameManager() { }

    public void setPuzzleBoard(PuzzleBoard board) {
        this.board = board;
    }

    public void newPuzzleBoard(List<Integer> newBoard) {
        this.board.updateBoard(newBoard);
    }

    public void requestAction() {
        System.out.println("Requesting CS...");
        pendingRequests = new ArrayList<>();
        numOK = 0;
        // increasing my logic clock
        Map<Integer, Integer> vClock = ClientsManager.get().getVectorClock();
        vClock.put(Server.getInstance().getPort(), vClock.get(Server.getInstance().getPort()) + 1);
        if (ClientsManager.get().getConnections().isEmpty()) {
            this.increaseOKCount();
        } else {
            // sending messages
            ClientsManager.get().getConnections().forEach((port, sessionService) -> {
                try {
                    sessionService.receiveRequestAction(Server.getInstance().getPort(), ClientsManager.get().getVectorClock());
                } catch (RemoteException e) {
                    ClientsManager.get().deleteSessionService(port);
                    e.printStackTrace();
                }
            });
        }
    }

    public void addPendingRequest(int port) {
        pendingRequests.add(port);
    }

    public static GameManager get() {
        return gameManager;
    }

    public void increaseOKCount() {
        numOK++;
        if (numOK >= ClientsManager.get().getConnections().size()) {
            System.out.println("Entering CS...");


            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            pendingRequests.forEach((port) -> {
                try {
                    ClientsManager.get().getConnection(port).receiveAction(board.getMap());
                    ClientsManager.get().getConnection(port).receiveActionOK();
                } catch (RemoteException e) {
                    ClientsManager.get().deleteSessionService(port);
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
