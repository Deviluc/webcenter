package core.plugin;

public interface Plugin {
	
	public void register(final PluginManager pluginManager);
	
	public PluginType getType();
	
	public Class<?> getIdentifierClass();

}
