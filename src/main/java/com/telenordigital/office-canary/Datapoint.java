package com.telenordigital.officecanary;

public interface Datapoint {}

class DatapointBase {
	public long id;
	public String deviceEUI;
	public java.time.Instant timestamp;
}
