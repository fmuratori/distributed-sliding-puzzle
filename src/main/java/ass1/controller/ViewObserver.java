package ass1.controller;

public interface ViewObserver {
	
	void notifyTaskStartRequest(String path, String stopwords);

	void notifyTaskStopRequest();
	
}
