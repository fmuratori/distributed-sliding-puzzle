package ass1.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ass1.controller.ModelObserver;
import ass1.controller.Utils;

public class Model {
	private static final long NANOSECONDS = 1000000000;
	private final int FILE_FINDERS = 1;
	private final int CHUNK_LOADERS = 6;
	private final int TEXT_PROCESSORS = 8;
	private final int BUFFER_SIZE = 5;

	private Flag taskStopFlag;
	private Dictionary dictManager;
	private ClosableBoundedBuffer<File> filesBuffer;
	private ClosableBoundedBuffer<String> chunksBuffer;
	private Barrier startTaskBarrier;
	private Latch stopTaskLatch;
	
	private FilesFinder fileFinder;
	private List<ChunkLoader> chunkLoaders;
	private List<TextProcessor> textProcessors;
	
	private long startNanoseconds;
	private long stopNanoseconds;

	private ModelObserver observer;

	public Model(ModelObserver observer) {
		this.observer = observer;
	}
	
	public void startTaskExecution(String directory, String stopwordsFile) {
		observer.notifyTaskStarting();
		
		// set task as started
		taskStopFlag = new Flag(); 
		dictManager = new Dictionary();
		
		// initialize shared data and synchronizaiton structures
		filesBuffer = new ClosableBoundedBuffer<>(BUFFER_SIZE);
		chunksBuffer = new ClosableBoundedBuffer<>(BUFFER_SIZE );
		startTaskBarrier = new Barrier(FILE_FINDERS + CHUNK_LOADERS + TEXT_PROCESSORS + 1); // +1 to synch current thread to start of task execution
		stopTaskLatch = new Latch(FILE_FINDERS + CHUNK_LOADERS + TEXT_PROCESSORS);
		
		// loa stopwords file
		List<String> stopwords = Utils.loadStopwords(stopwordsFile);

		// initialize active components (workers)
		fileFinder = new FilesFinder(
				directory,
				filesBuffer, 
				startTaskBarrier,
				stopTaskLatch,
				taskStopFlag);
		fileFinder.start();

		chunkLoaders = new ArrayList<>();
		for (int i = 0; i < CHUNK_LOADERS; i++) {
			ChunkLoader t = new ChunkLoader(
					filesBuffer, 
					chunksBuffer, 
					startTaskBarrier,
					stopTaskLatch,
					taskStopFlag);
			chunkLoaders.add(t);
			t.start();
		}
		
		textProcessors = new ArrayList<>();
		for (int i = 0; i < TEXT_PROCESSORS; i++) {
			TextProcessor t = new TextProcessor(
					dictManager, 
					chunksBuffer, 
					startTaskBarrier,
					stopTaskLatch,
					taskStopFlag, 
					stopwords);
			textProcessors.add(t);
			t.start();
		}

		// detect task execution start
		try {
			startTaskBarrier.hitAndWaitAll();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			startNanoseconds = System.nanoTime();
		}
		observer.notifyTaskStarted();
		
		// wait for task completion
		try {
			stopTaskLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			stopNanoseconds = System.nanoTime();
		}
		observer.notifyTaskCompleted();
	}


	public void stopTaskExecution() {
		taskStopFlag.setFlag();
		filesBuffer.close();
		chunksBuffer.close();
		observer.notifyTaskStopping();
	}
	
	public Map<String, Integer> getDictionary() {
		return dictManager.get();
	}

	public double taskCompletionPercentage() {
		return (double)chunkLoaders.stream().map(c -> c.getProcessedFilesCount()).reduce(0, (a, b)->a+b) 
				/ fileFinder.getProcessableFilesCount();
	}

	public double getExecutionTime() {
		return (double)(stopNanoseconds - startNanoseconds) / NANOSECONDS;
	}	
}
