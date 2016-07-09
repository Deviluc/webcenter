package core.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import sun.tools.jar.resources.jar;

public class PluginManager {
	
	private static PluginManager self;
	
	private Map<Class<?>, List<Plugin>> variations;
	
	private PluginManager() {
		variations = new HashMap<Class<?>, List<Plugin>>();
		
		loadInstalledPlugins();
	}
	
	public static PluginManager getInstance() {
		if (self == null) {
			self = new PluginManager();
		}
		
		return self;
	}
	
	
	
	
	public <T> boolean hasVariation(final Class<T> identifierClass) {
		return variations.containsKey(identifierClass);
	}
	
	public <T> List<T> getVarations(final Class<T> identifierClass) {
		List<T> result = new ArrayList<T>();
		
		if (variations.containsKey(identifierClass)) {
			variations.get(identifierClass).forEach(plugin -> result.add((T) plugin));
		}
		
		return result;
	}
	
	private void loadInstalledPlugins() {
		File pluginDir = new File("plugins/");
		
		Arrays.asList(pluginDir.listFiles()).forEach(file -> {
			try {
				JarFile jarFile = new JarFile(file);
				
				Manifest manifest = jarFile.getManifest();
				java.util.Collections.list(jarFile.entries()).stream().filter(e -> e.getName().matches(".*\\.class")).forEach(jarEntry -> {
					try {
						URLClassLoader loader = new URLClassLoader(new URL[]{file.toURL()});
						String name = jarFile.getName();
						if (name.contains("/")) {
							name = name.substring(name.lastIndexOf("/") + 1, name.length());
						}
						
						name = name.substring(0, name.lastIndexOf("."));
						
						Class<?> cl = loader.loadClass(name);
						
						for (Class<?> inter : cl.getInterfaces()) {
							if (inter.getName().equals("core.plugin.Plugin")) {
								register((Plugin) cl.newInstance());
								break;
							}
						}
						
						loader.close();
					} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					try {
						jarFile.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	private void register(final Plugin plugin) {
		
		switch (plugin.getType()) {
		case VARIATION:
			
			if (!variations.containsKey(plugin.getIdentifierClass())) {
				variations.put(plugin.getIdentifierClass(), new ArrayList<Plugin>());
			}
			
			variations.get(plugin.getIdentifierClass()).add(plugin);
			
			break;

		default:
			break;
		}
		
	}

}
