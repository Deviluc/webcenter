package model;

public class Triplet<T, U, V> {
	
	private T t;
	private U u;
	private V v;

	public Triplet(T t, U u, V v) {
		this.t = t;
		this.u = u;
		this.v = v;
	}
	
	public T getA() {
		return t;
	}
	
	public U getB() {
		return u;
	}
	
	public V getC() {
		return v;
	}

}
