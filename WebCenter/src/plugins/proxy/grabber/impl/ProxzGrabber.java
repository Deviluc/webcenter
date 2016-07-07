package plugins.proxy.grabber.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import core.db.ProxyDatabase;
import core.exceptions.NoElementFoundException;
import core.lib.HttpLib;
import core.lib.RegexLib;
import core.web.ConnectionHandler;
import model.Proxy;
import plugins.proxy.grabber.ProxyGrabber;

public class ProxzGrabber extends BaseProxyGrabber implements ProxyGrabber{
	
	private final ConnectionHandler connectionHandler;
	private final String baseUrl = "http://www.proxz.com/";
	private final String l1PageName = "proxy_list_high_anonymous_";
	private final String pageSuffix = ".html";
	
	
	public ProxzGrabber(ConnectionHandler connectionHandler, final ProxyDatabase db) {
		super(db);
		this.connectionHandler = connectionHandler;
	}

	@Override
	public List<Proxy> getProxies() throws Exception {
		List<Proxy> proxies = new ArrayList<Proxy>();
		int pageCount = getPageCount();
		
		for (int i = 0; i < pageCount; i++) {
			final HttpURLConnection connection = connectionHandler.connect(baseUrl + l1PageName + i + pageSuffix);	
			final String doc = HttpLib.getDocument(connection);
			
			final String script = RegexLib.getGroupMatch("eval\\((unescape\\('[^)]+\\))\\)", doc, 1) + ";";
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
			String scriptResult = (String) engine.eval(script);
			
			final List<String[]> proxyInfos = RegexLib.getAllGroupMatches("<td>([^<]+)</td><td>([^<]+)</td>", scriptResult, new int[]{1, 2});
			
			for (String[] proxyInfo : proxyInfos) {
				Proxy proxy = new Proxy(proxyInfo[0], proxyInfo[1]);
				proxy.setAnonymity(1);
				proxies.add(proxy);
				if (!saveProxy(proxy)) {
					System.err.println("Proxy already in database: " + proxy.toString());
				}
			}
		}
		
		return proxies;
	}
	
	private int getPageCount() throws MalformedURLException, IOException {
		HttpURLConnection connection = connectionHandler.connect(baseUrl + l1PageName + 0 + pageSuffix);
		
		if (connection.getResponseCode() == 200) {
			final String doc = HttpLib.getDocument(connection);
			
			try {
				final List<String> pageNumbers = RegexLib.getAllGroupMatches("<a\\shref='" +  l1PageName + "([0-9]+)" + pageSuffix, doc, 1);
				return pageNumbers.size() / 2;
			} catch (NoElementFoundException e) {
				e.printStackTrace();
				System.err.println("Website 'proxz.com' changed, please adapt grabber implementation");
				return 0;
			}
			
		} else {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String errorDetails = "";
			String line;
			
			while ((line = reader.readLine()) != null) {
				errorDetails += line + "\n";
			}
			
			throw new HttpRetryException(errorDetails.trim(), connection.getResponseCode());
		}
	}

}
