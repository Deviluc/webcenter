package core.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamLib {
	
	public static void copy(final InputStream in, final OutputStream out, final int bufferSize, final long readBytes) throws IOException {
		long bytesRead = 0;
		
		while (bytesRead < readBytes) {
			final long bytesToRead = readBytes - bytesRead;
			final byte[] buffer;
			
			if (bytesToRead < bufferSize) {
				buffer = new byte[(int) bytesToRead];
			} else {
				buffer = new byte[bufferSize];
			}
			
			int read = in.read(buffer);
			out.write(buffer, 0, read);
			bytesRead += read;
		}
	}

}
