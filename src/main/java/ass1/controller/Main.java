package ass1.controller;

public class Main {
	
	public static void main(String[] args) {
		new Controller().initializeWithGraphicUI();
	}

	/*
	 * BENCHMARKS 
	 * 10 pdf files
	 * 500 pages per file
	 * 
	 * Parameters:
	 * [1] pdf file finder
	 * [2, 4, 6] chunk loader
	 * [2, 4, 6, 8, 10] text processors
	 * 
	 * file finders - chunk loaders - text processors -> mean execution time
	 * 1-2-2 26.42, 26.96, 27.31 -> 26.9
	 * 1-2-4 28.72, 28.07, 27.56 -> 28.12
	 * 1-2-6 28.77, 27.38, 28.57 -> 28.24
	 * 1-2-8 29.63, 29.31, 29.17 -> 29.37
	 * 1-4-2 22.60, 23.40, 23.42 -> 23.14
	 * 1-4-4 23.77, 24.62, 23.64 -> 24.01
	 * 1-4-6 24.06, 24.86, 24.47 -> 24.46
	 * 1-4-8 23.00, 24.25, 26.46 -> 24.57
	 * 1-6-2 26.17, 25.95, 25.31 -> 25.81
	 * 1-6-4 24.99, 24.60, 24.52 -> 24.7
	 * 1-6-6 25.79, 24.31, 23.63 -> 24.58
	 * 1-6-8 24.48, 24.54, 24.79 -> 24.6
	 */
}
