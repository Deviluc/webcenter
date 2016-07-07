package model;

public class Pair<T, U> {
	
	private T t;
	private U u;
	
	public Pair(T t, U u) {
		this.t = t;
		this.u = u;
	}
	
	public T getA() {
		return t;
	}
	
	public U getB() {
		return u;
	}

}
