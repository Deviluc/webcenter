package core.db;

public interface DatabaseEntry<T> {

	
	public void setId(final int id);
	
	public int getId();
	
	public T getEntry();

}
