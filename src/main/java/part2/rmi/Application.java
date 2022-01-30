package part2.rmi;

import part2.rmi.remote.Server;

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

		final int n = 1;
		final int m = 2;
		
		final String imagePath = "src/main/resources/bletchley-park-mansion.jpg";
		
		final PuzzleBoard puzzle = new PuzzleBoard(n, m, imagePath);
        puzzle.setVisible(true);
	}
}
