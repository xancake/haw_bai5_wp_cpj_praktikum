package a1.p1;

public class A1P1Starter  {
	public static void main(String[] args) {
		MyThread vater = new MyThread(2, 5);
		vater.start();
		try {
			vater.join();
		} catch (InterruptedException e) {}
		System.out.println(vater.getThreadHierarchie());
	}
}
