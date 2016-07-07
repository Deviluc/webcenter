package core.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import core.lib.HttpLib;
import core.lib.StreamLib;

public class Downloader {
	
	private ConnectionHandler connectionHandler;

	public Downloader(final ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
	
	public void directDownload(final String url, final File file, final int maxThreads) throws MalformedURLException, IOException, InterruptedException {
		HttpURLConnection connection = connectionHandler.connect(url);
		
		if (connection.getResponseCode() != 200) {
			HttpLib.getException(connection);
		}
		
		final long contentLength = connection.getContentLengthLong();
		
		System.out.println("Content length: " + (contentLength / (1024 * 1024)));
		
		int threadCount = maxThreads;
		
		if (contentLength < maxThreads) {
			threadCount = (int) contentLength;
		}
		
		final CountDownLatch countdown = new CountDownLatch(threadCount);
		
		for (int i = 0; i < threadCount; i++) {
			final long skipBytes = i * (contentLength / threadCount);
			long readBytes = (contentLength / threadCount);
			
			if (i == (threadCount - 1)) {
				readBytes = contentLength - (i * readBytes);
			}
			
			final HttpURLConnection conn = connectionHandler.connect(url);
			conn.setRequestProperty("Range", "bytes=" + skipBytes + "-" + contentLength);
			
			
			Runnable partDownload = new PartDownload(i, conn, skipBytes, readBytes, countdown, new File(file.getAbsolutePath() + ".part" + i));
			new Thread(partDownload).start();
		}
		
		countdown.await(1, TimeUnit.DAYS);
		
		FileOutputStream outStream = new FileOutputStream(file);
		
		for (int i = 0; i < threadCount; i++) {
			File inFile = new File(file.getAbsolutePath() + ".part" + i);
			FileInputStream inStream = new FileInputStream(inFile);
			
			StreamLib.copy(inStream, outStream, 1024, inFile.length());
			
			inStream.close();
			inFile.delete();
		}
		
		outStream.close();		
	}
	
	private class PartDownload implements Runnable {
		
		private final int id;
		private final HttpURLConnection connection;
		private final long skipBytes, readBytes;
		private final CountDownLatch countdown;
		private final File destination;
		
		public PartDownload(final int id, final HttpURLConnection connection, final long skipBytes, final long readBytes, final CountDownLatch countdown, final File destination) throws IOException {
			
			if (connection.getResponseCode() != 206) {
				HttpLib.getException(connection);
			}
			
			this.id = id;
			this.connection = connection;
			this.skipBytes = skipBytes;
			this.readBytes = readBytes;
			this.countdown = countdown;
			this.destination = destination;
		}

		@Override
		public void run() {
			try {
				final InputStream inStream = connection.getInputStream();
				connection.getHeaderFields();
				final OutputStream outStream = new FileOutputStream(destination);
				
				StreamLib.copy(inStream, outStream, 1024, readBytes);
				
				inStream.close();
				outStream.close();				
			} catch (IOException e) {
				//Download failed!
				e.printStackTrace();
				System.out.println("Download with id " + id + "failed!");
			}
			
			countdown.countDown();			
		}
		
		
		
	}

}
