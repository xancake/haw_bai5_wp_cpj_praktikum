package a1.p1;

import java.util.ArrayList;
import java.util.List;

public class MyThread extends Thread {
	private List<MyThread> myChildren;
	
	private int myRekursionsAbbruch;
	private int myAnzahlThread;
	private int myRekursionsEbene;
	
	public MyThread(int anzahlThreads, int rekursionsAbbruchEbene) {
		this(anzahlThreads, 0, rekursionsAbbruchEbene);
	}
	
	private MyThread(int anzahlThread, int rekursionsEbene, int rekursionsAbbruchEbene) {
		myChildren = new ArrayList<>();
		myAnzahlThread = anzahlThread;
		myRekursionsEbene = rekursionsEbene;
		myRekursionsAbbruch = rekursionsAbbruchEbene;
		setName(createName(myRekursionsEbene));
		setPriority(calculatePriority(myRekursionsEbene, myRekursionsAbbruch));
	}
	
	@Override
	public void run() {
		if(myRekursionsEbene <= myRekursionsAbbruch) {
			for (int i = 0; i < myAnzahlThread; i++) {
				MyThread thread = new MyThread(myAnzahlThread, myRekursionsEbene+1, myRekursionsAbbruch);
				myChildren.add(thread);
				thread.start();
			}
			
			for(Thread thread : myChildren) {
				try {
					thread.join();
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public String getThreadHierarchie() {
		StringBuffer sb = new StringBuffer();
		appendThread(sb);
		return sb.toString();
	}
	
	private StringBuffer appendThread(StringBuffer sb) {
		sb.append(getName());
		sb.append("(");
		sb.append("id=").append(getId());
		sb.append(",");
		sb.append("priority=").append(getPriority());
		sb.append(")");
		for(MyThread child : myChildren) {
			sb.append("\n");
			for(int i=0; i<child.myRekursionsEbene; i++) {
				sb.append("  ");
			}
			child.appendThread(sb);
		}
		return sb;
	}
	
	private String createName(int rekursionsEbene) {
		switch(rekursionsEbene) {
			case 0: return "Vater";
			case 1: return "Sohn";
			case 2: return "Enkel";
			default: return "Ur" + createName(rekursionsEbene-1).toLowerCase();
		}
	}
	
	private int calculatePriority(int rekursionsEbene, int rekursionsAbbruch) {
		double percentage = rekursionsAbbruch * rekursionsEbene / 100D;
		return (int)((percentage * 100 / 9) + 1);
	}
}
