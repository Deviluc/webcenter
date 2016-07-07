package core.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import model.Proxy;
import model.ProxyFilter;

public class ProxyDatabase extends Database<Proxy> {
	
	private static ProxyDatabase proxyDatabase = null;

	private ProxyDatabase() {
		super();
	}
	
	public static ProxyDatabase getInstance() {
		if (proxyDatabase == null) {
			proxyDatabase = new ProxyDatabase();
		}
		
		return proxyDatabase;
	}
	
	public List<Proxy> searchProxies(final ProxyFilter proxyFilter) {
		final ArrayList<Proxy> proxies = new ArrayList<Proxy>();
		
		for (Proxy proxy : map.values()) {
			if (proxyFilter.matches(proxy)) {
				proxies.add(proxy);
			}
		}
		
		return proxies;
	}
	

}
