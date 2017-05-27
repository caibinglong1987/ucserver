package com.roamtech.uc.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Session implements Serializable {
	private String started;
	private String stopped;
	private String location;//<country name>:<country iso code>
	private String volume;
	public String getStarted() {
		return started;
	}
	public void setStarted(String started) {
		this.started = started;
	}
	public String getStopped() {
		return stopped;
	}
	public void setStopped(String stopped) {
		this.stopped = stopped;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	
}
