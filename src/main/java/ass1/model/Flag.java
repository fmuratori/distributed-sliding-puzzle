package ass1.model;

public class Flag {

	private boolean flag = false;
	
	synchronized public void setFlag() {
		flag = true;
	}
	
	synchronized public void unsetFlag() {
		flag = false;
	}

	synchronized public boolean isFlagSet() {
		return flag;
	}
	
}
