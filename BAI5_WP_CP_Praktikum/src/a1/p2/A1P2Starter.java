package a1.p2;

public class A1P2Starter {
	public static void main(String[] args) throws InterruptedException {
		Thread busyThread = new BusyThread();
		Thread messThread = new MessThread();
		
		messThread.setPriority(10);
		busyThread.setPriority(5);
		
		busyThread.start();
		messThread.start();
		messThread.join();
		busyThread.interrupt();
	}
	
	private static class MessThread extends Thread {
		public MessThread() {
			super("Messthread");
		}
		
		@Override
		public void run() {
			long before = System.nanoTime();
			yield();
			long diff = System.nanoTime()-before;
			System.out.println("Zeitscheibenlänge: " + diff + "ns");
		}
	}
	
	private static class BusyThread extends Thread {
		public BusyThread() {
			super("Busythread");
		}
		
		@Override
		public void run() {
			int x = 1;
			while(!isInterrupted()) {
				x = x*x + x + 1;
			}
		}
	}
}
