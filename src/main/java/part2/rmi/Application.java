package part2.rmi;

import part2.rmi.remote.ClientsManager;
import part2.rmi.remote.Server;

import java.net.BindException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Simple Puzzle Game - Centralized version.
 * 
 * By A. Croatti 
 * 
 * @author acroatti
 *
 */
public class Application {

	public static void main(final String[] args) {



		System.out.println("Initializing the puzzle game...");

		final int n = 2;
		final int m = 2;

		final String imagePath = "src/main/resources/bletchley-park-mansion.jpg";

		final PuzzleBoard puzzle = new PuzzleBoard(n, m, imagePath);
		puzzle.setVisible(true);


		System.out.println("Initializing distributed data structures ...");
		List<Integer> sockets = new ArrayList<>(Arrays.asList(12345, 12346, 12347));

		for (Integer port : sockets) {
			try {
				Server.initialize(port);

				if (port != 12345) {
					ClientsManager.get().connect(12345);

				}
				// TODO: sistemare chiusura connessione
//				ClientsManager.get().disconnect();
//				Server.getInstance().terminate();
//				ClientsManager.get().terminate();
				break;
			} catch (RemoteException e) {
				e.printStackTrace();
				System.out.println("Port " + port + " already in use.");
			}
		}
	}
}
