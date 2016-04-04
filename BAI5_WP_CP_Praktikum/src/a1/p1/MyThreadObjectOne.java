package a1.p1;


public class MyThreadObjectOne implements Runnable {
	
	private volatile boolean myRunning = true;
	
	@Override
	public void run() {
	
		while(myRunning) {
	
		}
		
	}
	
	public boolean isRunning(){
		return myRunning;
	}
	
	public void myTerminate(boolean running){
		myRunning = running;
	}
	
}
