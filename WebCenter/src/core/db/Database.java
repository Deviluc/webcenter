package core.db;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import model.Procedure;

public class Database<T> {
	
	private final List<Procedure> dbChangeHooks;
	private final DB db;
	protected final HTreeMap<Integer, T> map;

	public Database() {
		db = DBMaker.newFileDB(new File(System.getProperty("user.dir") + "/db")).closeOnJvmShutdown().make();
		String name = getGenericName();
		System.out.println("DB-Name: " + name);
		map = db.getHashMap(name);
		dbChangeHooks = new ArrayList<Procedure>();
	}
	
	public boolean has(final DatabaseEntry<T> entry) {
		return map.containsValue(entry);
	}
	
	public void create(final DatabaseEntry<T> entry) {
		int id = map.keySet().size();
		entry.setId(id);
		insert(entry);
		fireDbChange();
	}
	
	public void update(final DatabaseEntry<T> entry) {
		if (entry.getId() != -1 && map.containsKey(entry.getId())) {
			map.remove(entry.getId());
			map.put(entry.getId(), entry.getEntry());
			db.commit();
			fireDbChange();
		}	
	}
	
	public void delete(final DatabaseEntry<T> entry) {
		map.remove(entry.getId());
		db.commit();
		fireDbChange();
	}
	
	public T get(final int id) {
		return map.get(id);
	}
	
	public Collection<T> getCollection() {
		return map.values();
	}
	
	
	protected void insert(final DatabaseEntry<T> entry) {
		map.put(entry.getId(), entry.getEntry());
		db.commit();
		fireDbChange();
	}

	
	public DB getDB() {
		return db;
	}
	
	public void addChangeListener(final Procedure listener) {
		dbChangeHooks.add(listener);
	}
	
	private String getGenericName() {
	    return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName();
	}
	
	protected void fireDbChange() {
		for (Procedure listener : dbChangeHooks) {
			listener.execute();
		}
	}

}
