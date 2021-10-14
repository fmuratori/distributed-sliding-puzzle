package ass1.controller;

import ass1.model.Model;
import ass1.view.View;

public class Controller implements ViewObserver, ModelObserver {

	private final int UPDATES_MILLISECONDS = 1000;
	
	private View view;
	private Model model;
	
	private Thread modelPoller;
	private boolean blockPoller;

	public void initializeWithGraphicUI() {
		view = new View(this);
	}
	
	/* view -> model events */
	
	@Override
	public void notifyTaskStartRequest(String path, String stopwords) {
		model = new Model(this);
		new Thread(() -> model.startTaskExecution(path, stopwords)).start();
	}

	@Override
	public void notifyTaskStopRequest() {
		model.stopTaskExecution();
	}
	
	/* model -> view events */

	@Override
	public void notifyTaskStarting() {
		view.handleStartingExecution();
	}

	@Override
	public void notifyTaskStarted() {
		view.handleStartedExecution();
		blockPoller = false;
		modelPoller = new Thread(() -> {
			while (!blockPoller) {
				try {
					view.handleUpdate(Utils.sortMapByValue(model.getDictionary()), model.taskCompletionPercentage());
					Thread.sleep(UPDATES_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		modelPoller.start();
	}

	@Override
	public void notifyTaskStopping() {
		view.handleStoppingExecution();
	}

	@Override
	public void notifyTaskCompleted() {
		view.handleCompletedExecution(Utils.sortMapByValue(model.getDictionary()), model.getExecutionTime());
		try {
			blockPoller = true;
			modelPoller.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
