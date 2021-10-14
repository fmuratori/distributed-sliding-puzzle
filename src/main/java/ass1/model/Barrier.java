package ass1.model;

public class Barrier {

	private int nParticipants;
	private int nHits;

	public Barrier(int nParticipants) {
		this.nParticipants = nParticipants;
		nHits = 0;
	}
	
	public synchronized void hitAndWaitAll() throws InterruptedException {	
		nHits++;
		if (isCompleted()) {
			notifyAll();
			onComplete();
		} else { 
			while (nHits < nParticipants) {
				wait();
			}
		}
	}
	
	public void onComplete() {}
	
	public synchronized boolean isCompleted() {
		return nHits == nParticipants;
	}

}