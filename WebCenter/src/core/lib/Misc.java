package core.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;

public abstract class Misc {

	public static String getProperty(final String resource, final String key) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		File resourceFile = new File(System.getProperty("user.dir") + "/resources/" + resource + ".properties");
		properties.load(new FileInputStream(resourceFile));
		return properties.getProperty(key).trim();
	}
	
	public static Image loadImage(final String name) {
		return new Image("file:" + System.getProperty("user.dir") + "/resources/images/" + name);
	}
	
	public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
	
	public static Map<Integer, Map<String, String>> loadResourceEnumeration(final String resourceName) {
		Map<Integer, Map<String, String>> list = new HashMap<Integer, Map<String, String>>();
		
		Properties properties = new Properties();
		File resourceFile = new File(System.getProperty("user.dir") + "/resources/" + resourceName + ".properties");
		
		try {
			properties.load(new FileInputStream(resourceFile));
			Pattern pattern = Pattern.compile("[^.]+\\.([0-9])\\.(.+)");
			
			properties.forEach((k, v) -> {
				Matcher matcher = pattern.matcher((String) k);
				
				if (matcher.find()) {
					
					int num = Integer.parseInt(matcher.group(1));
					
					if (!list.containsKey(num)) {
						list.put(num, new HashMap<String, String>());
					}
					
					list.get(num).put(matcher.group(2), (String) v);
				}
				
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return list;
	}

}
