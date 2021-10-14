package ass1.model;

import java.util.Optional;

public class ClosableBoundedBuffer<T> {

	private T[] buffer;
	private int in; // points to the next free position
	private int out; // points to the next full position

	private int numRegisteredProducers;
	private boolean stop;
	
	@SuppressWarnings("unchecked")
	public ClosableBoundedBuffer(int size) {
		in = 0;
		out = 0;
		buffer = (T[]) new Object[size];
		stop = false;
	}

	public synchronized void put(T item) throws InterruptedException {
		while (isFull() && !stop) {
			wait();
		}
		
		if (!stop) {
			buffer[in] = item;
			in = (in + 1) % buffer.length;
			notifyAll();
		}
	}

	public synchronized Optional<T> get() throws InterruptedException {
		while (isEmpty() && numRegisteredProducers > 0 && !stop) {
			wait();
		}
		if (!stop) {
			if (!isEmpty()) {
				T item = buffer[out];
				out = (out + 1) % buffer.length;
				notifyAll();
				return Optional.of(item);
			} else { 
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	private boolean isFull() {
		return (in + 1) % buffer.length == out;
	}

	private boolean isEmpty() {
		return in == out;
	}
	
	public synchronized void subscribeProducer() {
		numRegisteredProducers++;
	}
	
	public synchronized void unsubscribeProducer() {
		numRegisteredProducers--;
		if (numRegisteredProducers == 0) {
			notifyAll();
		}
	}
	
	public synchronized void close() {
		stop = true;
		notifyAll();
	}
}
