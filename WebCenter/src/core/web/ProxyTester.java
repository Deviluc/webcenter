package core.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import core.db.ProxyDatabase;
import core.lib.Misc;
import javafx.util.Pair;
import model.Proxy;

public class ProxyTester {
	
	private URL testURL;
	private String testContent;
	private static String testTitle = "<TITLE>Blank site - Blanksite.com - Nothing to see here - Check Pointless.com</TITLE>";

	public ProxyTester() throws MalformedURLException, IOException {
		testURL = new URL("http://www.blanksite.com/");
		testContent = downloadTestContent((HttpURLConnection) testURL.openConnection()).getValue();
	}
	
	public void checkDatabase(final ProxyDatabase db, final int maxTimeout, final int threads) throws InterruptedException {
		List<Proxy> syncedProxies = Collections.synchronizedList(new ArrayList<Proxy>(db.getCollection()));
		
		final CountDownLatch latch = new CountDownLatch(threads);
		
		for (int i = 0; i < threads; i++) {
			new Thread(new ProxyCheckerThread(syncedProxies, maxTimeout, latch)).start();
		}
		
		latch.await((syncedProxies.size() / threads) * maxTimeout * 2, TimeUnit.MILLISECONDS);
	}
	
	public List<Proxy> getWorkingProxies(final List<Proxy> proxies, final int timeout) {
		final List<Proxy> workingProxies = new ArrayList<Proxy>();
		
		for (Proxy proxy : proxies) {
			if (testProxy(proxy, timeout)) {
				workingProxies.add(proxy);
			}
		}
		
		return workingProxies;
	}
	
	public List<Proxy> getWorkingProxies(final List<Proxy> proxies, final int timeout, final int maxThreads) throws InterruptedException {
		int threadCount = maxThreads;
		
		if (threadCount > proxies.size()) {
			threadCount = proxies.size();
		}
		
		final List<Proxy> synchronizedProxies = Collections.synchronizedList(proxies);
		final List<Proxy> workingProxies = Collections.synchronizedList(new ArrayList<Proxy>());
		final CountDownLatch latch = new CountDownLatch(threadCount);
				
		for (int i = 0; i < threadCount; i++) {
			new Thread(new ProxyCheckerThread(synchronizedProxies, workingProxies, timeout, latch)).start();
		}
		latch.await((proxies.size() / maxThreads) * timeout * 2, TimeUnit.MILLISECONDS);
		return workingProxies;
	}
	
	
	private boolean testProxy(final Proxy proxy, final int timeout) {
		try {
			HttpURLConnection connection = (HttpURLConnection) testURL.openConnection(proxy.toJavaProxy());
			connection.setConnectTimeout(timeout);
			final Pair<Integer, String> timeoutContentPair = downloadTestContent(connection);
			final String content = timeoutContentPair.getValue();
			
			if (content.equals(testContent)) {
				proxy.setContentModified(false);
				proxy.setTimeout(timeoutContentPair.getKey());
				proxy.setWorking(true);
				ProxyDatabase.getInstance().update(proxy);
				return true;
			} else if (content.contains(testTitle)) {
				proxy.setContentModified(true);
				proxy.setTimeout(timeoutContentPair.getKey());
				proxy.setWorking(true);
				ProxyDatabase.getInstance().update(proxy);
				return true;
			} else {
				proxy.setWorking(false);
			}
		} catch (Exception e) {
			if (e.getClass().equals(java.net.SocketTimeoutException.class)) {
				if (proxy.getTimeout() < timeout) {
					proxy.setWorking(false);
				} else {
					System.err.println("ELSE timeout=" + timeout + ";proxyTimeout=" + proxy.getTimeout() + ";working=" + proxy.getIsWorking());
				}
			} else {
				proxy.setWorking(false);
			}
		}
		
		ProxyDatabase.getInstance().update(proxy);
		
		
		return false;		
	}
	
	private Pair<Integer, String> downloadTestContent(final HttpURLConnection connection) throws IOException {
		final long startTime = System.currentTimeMillis();
		connection.connect();
		final int timeout = (int) (System.currentTimeMillis() - startTime);
		
		if (connection.getResponseCode() == 200) {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String content = "";
			String line;
			
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
			
			return new Pair<Integer, String>(timeout, content.trim());			
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
	
	private class ProxyCheckerThread implements Runnable {
		
		final List<Proxy> proxies;
		final List<Proxy> workingProxies;
		final int timeout;
		final CountDownLatch countdown;
		
		public ProxyCheckerThread(final List<Proxy> proxies, final List<Proxy> workingProxies, final int timeout, final CountDownLatch countdown) {
			this.proxies = proxies;
			this.workingProxies = workingProxies;
			this.timeout = timeout;
			this.countdown = countdown;
		}
		
		public ProxyCheckerThread(final List<Proxy> proxies, final int timeout, final CountDownLatch countdown) {
			this.proxies = proxies;
			this.workingProxies = null;
			this.timeout = timeout;
			this.countdown = countdown;
		}

		@Override
		public void run() {
			while (proxies.size() > 0) {
				Proxy proxy = proxies.remove(0);
				if (testProxy(proxy, timeout) && workingProxies != null) {
					workingProxies.add(proxy);
				}
				
				System.out.println(proxy.toString() + "\tworking: " + proxy.getIsWorking() + "\ttimeout: " + proxy.getTimeout() + "\tcontentModified: " + proxy.getContentModified());
			}
			
			countdown.countDown();
		}
		
		
	}

}
