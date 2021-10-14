package ass1.model;

import java.util.List;
import java.util.Optional;

import ass1.controller.Utils;

public class TextProcessor extends Thread {

	private Dictionary dictManager;
	private ClosableBoundedBuffer<String> chunksBuffer;
	private Barrier startTaskBarrier;
	private Latch stopTaskLatch;
	private Flag stopFlag;
	private List<String> stopwords;

	public TextProcessor(Dictionary dictManager, ClosableBoundedBuffer<String> chunksBuffer,
			Barrier startTaskBarrier, Latch stopTaskLatch, Flag taskStateFlag, List<String> stopwords) {
		this.dictManager = dictManager;
		this.chunksBuffer = chunksBuffer;
		this.startTaskBarrier = startTaskBarrier;
		this.stopTaskLatch = stopTaskLatch;
		this.stopFlag = taskStateFlag;
		this.stopwords = stopwords;
	}

	@Override
	public void run() {
		try {
			log("Waiting for other threads to initialize ...");
			startTaskBarrier.hitAndWaitAll();
			while (true) {
				Optional<String> text = chunksBuffer.get();
				if (text.isPresent() && !stopFlag.isFlagSet())
					dictManager.update(Utils.getWordsCount(text.get(), stopwords));
				else 
					break;
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		stopTaskLatch.countDown();
		log("Terminated thread");
	}

	private void log(String message) {
		System.out.println("[TEXT PROCESSOR] " + toString() + " - " + message);
	}

	public double getCompletionPercentage() {
		return 100;
	}
}
