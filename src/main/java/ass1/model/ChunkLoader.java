package ass1.model;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class ChunkLoader extends Thread {
	private ClosableBoundedBuffer<File> filesBuffer;
	private ClosableBoundedBuffer<String> chunksBuffer;
	private Barrier startTaskBarrier;
	private Latch stopTaskLatch;
	private Flag stopFlag;

	private int processedFilesCount;

	public ChunkLoader(ClosableBoundedBuffer<File> filesBuffer, ClosableBoundedBuffer<String> chunksBuffer, Barrier startTaskBarrier, Latch stopTaskLatch, Flag flag) {
		this.filesBuffer = filesBuffer;
		this.chunksBuffer = chunksBuffer;
		this.startTaskBarrier = startTaskBarrier;
		this.stopTaskLatch = stopTaskLatch;
		this.stopFlag = flag;
		this.processedFilesCount = 0;
	}

	@Override
	public void run() {
		try {
			log("Waiting for other threads to initialize ...");
			chunksBuffer.subscribeProducer();
			startTaskBarrier.hitAndWaitAll();
			while (true) {
				Optional<File> file = filesBuffer.get();
				if (file.isPresent() && !stopFlag.isFlagSet()) {
					PDDocument document = PDDocument.load(file.get());
					PDFTextStripper stripper = new PDFTextStripper();
					
					// load file chunks
					for (int p = 1; p <= document.getNumberOfPages(); ++p) {
						stripper.setStartPage(p);
						stripper.setEndPage(p);
						String text = stripper.getText(document);
						chunksBuffer.put(text);
					}
					
					// load the entire file
//					String text = stripper.getText(document);
//					chunksBuffer.put(text);
					
					document.close();
					processedFilesCount++;
				} else {
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		chunksBuffer.unsubscribeProducer();
		stopTaskLatch.countDown();
		log("Terminated thread");
	}

	private void log(String message) {
		System.out.println("[CHUNK LOADER] " + toString() + " - " + message);
	}

	public int getProcessedFilesCount() {
		return processedFilesCount;
	}

}
