package plugins.proxy.grabber;

import java.util.List;
import model.Proxy;

public interface ProxyGrabber {
	
	public List<Proxy> getProxies() throws Exception;

}
