package a1.p1;


public class MyThread extends Thread {
	
	public static void main(String[] args) {
		MyThread mt = new MyThread();
		mt.start();
	}
	
	public void run() {
		
		while(!isInterrupted()) {
			MyThreadObjectOne mtoo = new MyThreadObjectOne();
			Thread t1 = new Thread(mtoo);
			t1.setName("ThreadOne");
			t1.start();
		}
		
	}
	
}
