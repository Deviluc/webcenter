package model;

public class DisplayValue<T> {
	
	private final String displayName;
	private final T value;
	
	public DisplayValue(final String displayName, final T value) {
		this.displayName = displayName;
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
	@Override
	public String toString() {
		return displayName;
	}

}
