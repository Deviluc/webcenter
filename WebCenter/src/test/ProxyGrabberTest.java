package test;

import java.util.List;

import org.testng.annotations.Test;

import core.db.ProxyDatabase;
import core.web.ConnectionHandler;
import core.web.ProxyTester;
import model.Proxy;
import model.ProxyFilter;
import plugins.proxy.grabber.impl.ProxyListenGrabber;
import plugins.proxy.grabber.impl.ProxzGrabber;

public class ProxyGrabberTest {
	
	@Test
	public void testProxzGrabber() throws Exception {
		List<Proxy> proxies = new ProxzGrabber(new ConnectionHandler(), ProxyDatabase.getInstance()).getProxies();
		System.out.println("Proxies found: " + proxies.size());
		List<Proxy> workingProxies = new ProxyTester().getWorkingProxies(proxies, 6000, 50);
		System.out.println("Working: " + workingProxies.size());
	}
	
	@Test
	public void testProxyListenGrabber() throws Exception {
		List<Proxy> proxies = new ProxyListenGrabber(new ConnectionHandler(), ProxyDatabase.getInstance()).getProxies();
		System.out.println("Proxies found: " + proxies.size());
		List<Proxy> workingProxies = new ProxyTester().getWorkingProxies(proxies, 6000, 50);
		System.out.println("Working: " + workingProxies.size());
	}
	
	@Test (dependsOnMethods = {"testProxzGrabber", "testProxyListenGrabber"})
	public void testDatabase() throws Exception {
		ProxyDatabase db = ProxyDatabase.getInstance();
		new ProxyTester().checkDatabase(db, 10000, 600);
		System.out.println("Proxy-count: " + db.getCollection().size());
		System.out.println("Working proxies: " + db.searchProxies(new ProxyFilter().setWorking(true)).size());
	}
	
	

}
