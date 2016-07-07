package plugins.proxy.grabber.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import core.db.ProxyDatabase;
import core.exceptions.NoElementFoundException;
import core.lib.HttpLib;
import core.lib.RegexLib;
import core.web.ConnectionHandler;
import model.Proxy;
import plugins.proxy.grabber.ProxyGrabber;

public class ProxyListenGrabber extends BaseProxyGrabber implements ProxyGrabber {
	
	final ConnectionHandler connectionHandler;
	final String baseUrl = "http://www.proxy-listen.de/Proxy/Proxyliste.html";

	public ProxyListenGrabber(final ConnectionHandler connectionHandler, final ProxyDatabase database) {
		super(database);
		this.connectionHandler = connectionHandler;
	}

	@Override
	public List<Proxy> getProxies() throws MalformedURLException, IOException {
		final List<Proxy> proxies = new ArrayList<Proxy>();
		String doc = "";
		
		
		for (int i = 1; i < 4; i++) {
			try {
				HttpURLConnection connection = connectionHandler.connect(baseUrl);
				doc = HttpLib.getDocument(connection);
				
				List<String[]> hiddenValue = RegexLib.getAllGroupMatches("<input\\sname=\"(.*)\"\\svalue=\"(.*)\"\\stype=\"hidden\"\\/>", doc, 1, 2);
				String[] nameValue = hiddenValue.get(0);
				String name = nameValue[0];
				String value = nameValue[1];
				String postData = "filter_port=&filter_http_gateway=&filter_http_anon=" + i +"&filter_http_response_time=&" + name + "=" + value + "&filter_country=&liststyle=leech&proxies=300&type=http&submit=Show";
				
				doc = formPost(postData);
				
				List<String[]> ipPortList = RegexLib.getAllGroupMatches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}):([0-9]+)", doc, 1, 2);
				
				for (String[] ipPort : ipPortList) {
					Proxy proxy = new Proxy(ipPort[0], ipPort[1]);
					proxy.setAnonymity(i);
					proxies.add(proxy);
					saveProxy(proxy);
				}
				
				String inputTag = RegexLib.getMatch("<input[^>]*onclick=\"nextPage\\(\\);\"[^>]*>", doc);
				
				while (!inputTag.contains("disabled")) {
					doc = formPost(postData.replace("submit=Show", "next=Next page"));
					
					ipPortList = RegexLib.getAllGroupMatches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}):([0-9]+)", doc, 1, 2);
					
					boolean doBreak = false;
					
					for (String[] ipPort : ipPortList) {
						Proxy proxy = new Proxy(ipPort[0], ipPort[1]);
						proxy.setAnonymity(i);
						proxies.add(proxy);
						
						if (!saveProxy(proxy)) {
							doBreak = true;
						}
					}
					
					if (doBreak) {
						break;
					}
					
					inputTag = RegexLib.getMatch("<input[^>]*onclick=\"nextPage\\(\\);\"[^>]*>", doc);
				}
				
				
			} catch (NoElementFoundException e) {
				System.out.println("doc:\n" + doc);
				e.printStackTrace();
			}
		}
		
		return proxies;
	}
	
	private String formPost(final String postData) throws MalformedURLException, IOException {
		final HttpURLConnection connection =  connectionHandler.connect(baseUrl);
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", Integer.toString(postData.length()));
		
		OutputStream out = connection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(postData);
		writer.flush();
		writer.close();
		
		connection.connect();
		
		return HttpLib.getDocument(connection);
	}

}
