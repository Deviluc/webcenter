package core.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;

public abstract class HttpLib {

	public static String getDocument(final HttpURLConnection connection) throws IOException, HttpRetryException {
		
		if (connection.getResponseCode() == 200) {
			return read(connection.getInputStream());
		} else {
			throw new HttpRetryException(read(connection.getErrorStream()), connection.getResponseCode());
		}
	}
	
	public static String getException(final HttpURLConnection connection) throws IOException, HttpRetryException {
		throw new HttpRetryException(read(connection.getErrorStream()), connection.getResponseCode()); 
	}
	
	private static String read(final InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String result = "";
		String line;
		
		while ((line = reader.readLine()) != null) {
			result += line + "\n";
		}
		
		return result.trim();
	}

}
