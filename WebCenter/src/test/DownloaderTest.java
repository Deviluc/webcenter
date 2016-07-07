package test;

import java.io.File;

import org.testng.annotations.Test;

import core.web.ConnectionHandler;
import core.web.Downloader;

public class DownloaderTest {

	public DownloaderTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testDownloader() throws Exception {
		Downloader downloader = new Downloader(new ConnectionHandler());
		File file = new File("/home/lucas/50meg.test");
		downloader.directDownload("http://mirror.internode.on.net/pub/test/50meg.test", file, 10);
		file.delete();
	}

}
