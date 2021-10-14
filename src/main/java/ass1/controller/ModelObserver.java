package ass1.controller;

public interface ModelObserver {
	void notifyTaskStarting();
	void notifyTaskStarted();
	void notifyTaskStopping();
	void notifyTaskCompleted(); 
}
