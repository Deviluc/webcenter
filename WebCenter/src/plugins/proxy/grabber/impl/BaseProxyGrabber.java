package plugins.proxy.grabber.impl;

import core.db.ProxyDatabase;
import model.Proxy;

public class BaseProxyGrabber {
	
	private final ProxyDatabase db;

	public BaseProxyGrabber(final ProxyDatabase db) {
		this.db = db;
	}
	
	public boolean saveProxy(final Proxy proxy) {
		if (!db.has(proxy)) {
			db.create(proxy);
			return true;
		}
		
		return false;
	}

}
