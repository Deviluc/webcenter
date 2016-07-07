package model;

import java.io.Serializable;
import java.net.InetSocketAddress;

import core.db.DatabaseEntry;

public class Proxy implements Serializable, DatabaseEntry<Proxy> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3500570290139783384L;
	private final String ip, port;
	private int anonymity, timeout;
	private int id;
	private boolean isWorking, isSelected, contentModified;

	public Proxy(final String ip, final String port) {
		this.ip = ip;
		this.port = port;
		anonymity = -1;
		timeout = -1;
		id = -1;
		isWorking = false;
		isSelected = false;
		contentModified = false;
	}
	
	public void setId(final int id) {
		this.id = id;
	}
	
	public void setAnonymity(final int anonymity) {
		this.anonymity = anonymity;
	}
	
	public void setWorking(final boolean isWorking) {
		this.isWorking = isWorking;
	}
	
	public void setContentModified(final boolean contentModified) {
		this.contentModified = contentModified;
	}
	
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}
	
	public void setIsSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean getIsWorking() {
		return isWorking;
	}
	
	public boolean getContentModified() {
		return contentModified;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getPort() {
		return port;
	}
	
	public int getAnonymity() {
		return anonymity;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public boolean getIsSelected() {
		return isSelected;
	}
	
	public java.net.Proxy toJavaProxy() {
		return new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port)));
	}
	
	@Override
	public String toString() {
		return ip + ":" + port;
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object.getClass() == this.getClass()) {
			Proxy proxy = (Proxy) object;
			try {
				if (proxy.getIp().equals(this.ip) && proxy.getPort().equals(this.port)) {
					return true;
				}
			} catch (NullPointerException e) {
			}
		}
		
		return false;
	}

	@Override
	public Proxy getEntry() {
		return this;
	}

}
