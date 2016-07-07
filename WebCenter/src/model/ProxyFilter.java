package model;

public class ProxyFilter {
	
	private Boolean isWorking, isSelected, modifiedContent;
	private Integer maxTimeout, maxAnonymity;

	public ProxyFilter() {
		isWorking = null;
		isSelected = null;
		modifiedContent = null;
		maxTimeout = null;
		maxAnonymity = null;
	}
	
	public ProxyFilter setWorking(final boolean isWorking) {
		this.isWorking = isWorking;
		return this;
	}
	
	public ProxyFilter setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
		return this;
	}
	
	public ProxyFilter setModifiedContent(final boolean modifiedContent) {
		this.modifiedContent = modifiedContent;
		return this;
	}
	
	public ProxyFilter setMaxTimeout(final int maxTimeout) {
		this.maxTimeout = maxTimeout;
		return this;
	}
	
	public ProxyFilter setMaxAnonymity(final int maxAnonymity) {
		this.maxAnonymity = maxAnonymity;
		return this;
	}
	
	public boolean matches(final Proxy proxy) {
		
		if (!((isWorking != null) ? isWorking == proxy.getIsWorking() : true)) {
			return false;
		}
		
		if (!((isSelected != null) ?  isSelected == proxy.getIsSelected() : true)) {
			return false;
		}
		
		if (!((modifiedContent != null) ?  modifiedContent == proxy.getContentModified() : true)) {
			return false;
		}
		
		if (!((maxTimeout != null) ? (proxy.getTimeout() != -1 && proxy.getTimeout() <= maxTimeout) : true)) {
			return false;
		}
		
		if (!((maxAnonymity != null) ? (proxy.getAnonymity() != -1 && proxy.getAnonymity() <= maxAnonymity) : true)) {
			return false;
		}
		
		return true;
	}

}
