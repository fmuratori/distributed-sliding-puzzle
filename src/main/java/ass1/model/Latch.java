package ass1.model;

public class Latch {

	private int count;
	
	public Latch(int count) {
		this.count = count;
	}
	
	public synchronized void await() throws InterruptedException {		
		while (count > 0) {
			wait();
		}
	}

	public synchronized void countDown() {	
		if (count > 0) {
			count--;
			if (count == 0) {
				notifyAll();
			}
		}
	}

	
}
