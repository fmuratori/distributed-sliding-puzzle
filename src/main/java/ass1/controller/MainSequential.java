package ass1.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import ass1.model.Dictionary;

public class MainSequential {
	
	/*
	 * BENCHMARKS 
	 * 10 pdf files
	 * 500 pages per file
	 * 
	 * 47.04, 47.33, 45.44 -> 46.60
	 * 
	 * */
	
	private static final int NANOSECONDS = 1000000000;
	final static String stopwordsFile = "./data/stopwords.txt";
	final static String directory = "./data/";
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		List<String> stopwords = Utils.loadStopwords(stopwordsFile);
		List<File> files = Utils.searchPDFFilesInDirectory(directory);
		
		Dictionary dictionary = new Dictionary();
		
		for (File file : files) {
			System.out.println("Processing file " + file.getName() + " ...");
			try {
				PDDocument document = PDDocument.load(file);
				PDFTextStripper stripper = new PDFTextStripper();
				for (int p = 1; p <= document.getNumberOfPages(); ++p) {
					stripper.setStartPage(p);
					stripper.setEndPage(p);
					String text = stripper.getText(document);
					dictionary.update(Utils.getWordsCount(text, stopwords));
					
				}
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		long stopTime = System.nanoTime();
		
		System.out.println("Execution time: " + (double)(stopTime - startTime)/NANOSECONDS);
		System.out.println(Utils.stringifyMap(Utils.sortMapByValue(dictionary.get()), 10));
		
	}
}
