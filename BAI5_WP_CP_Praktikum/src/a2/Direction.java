package a2;

public enum Direction {
	LEFT,
	RIGHT;
	
	public Direction swap() {
		return LEFT == this ? RIGHT : LEFT;
	}
}
