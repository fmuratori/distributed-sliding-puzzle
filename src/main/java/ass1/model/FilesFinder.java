package ass1.model;

import java.io.File;
import java.util.List;

import ass1.controller.Utils;

public class FilesFinder extends Thread {
	private ClosableBoundedBuffer<File> filesBuffer;
	private Barrier startTaskBarrier;
	private Latch stopTaskLatch;
	private Flag stopTask;
	
	private String directory;
	private int totalFilesCount;

	public FilesFinder(String directory, ClosableBoundedBuffer<File> filesBuffer, Barrier startTaskBarrier, Latch stopTaskLatch, Flag flag) {
		this.directory = directory;
		this.filesBuffer = filesBuffer;
		this.startTaskBarrier = startTaskBarrier;
		this.stopTaskLatch = stopTaskLatch;
		this.stopTask = flag;
		totalFilesCount = 0;
	}

	@Override
	public void run() {
		log("Scanning folder " + directory + " ...");
		filesBuffer.subscribeProducer();
		
		List<File> files = Utils.searchPDFFilesInDirectory(directory);
		totalFilesCount = files.size();

		log("Found " + totalFilesCount + " processable files");
		log("Waiting for other threads to initialize ...");
		
		try {
			startTaskBarrier.hitAndWaitAll();
			for (File elem : files) {
	        	if (stopTask.isFlagSet()) {
	        		break;
	        	}
	    		String fileName = elem.getName();
	        	if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("pdf")) {
	        		filesBuffer.put(elem);
	        	}
	        }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stopTaskLatch.countDown();
		filesBuffer.unsubscribeProducer();
		log("Terminated thread");
	}
	
	private void log(String message) {
		System.out.println("[FILES FINDER] " + toString() + " - " + message);
	}
	
	public synchronized int getProcessableFilesCount() {
		return totalFilesCount;
	}
	
}
