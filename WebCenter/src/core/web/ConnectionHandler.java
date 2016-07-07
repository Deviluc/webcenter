package core.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class ConnectionHandler {

	public ConnectionHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public HttpURLConnection connect(final String url) throws MalformedURLException, IOException {
		return (HttpURLConnection) new URL(url).openConnection();
	}

}
