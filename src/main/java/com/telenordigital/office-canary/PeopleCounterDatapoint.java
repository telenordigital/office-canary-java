package com.telenordigital.officecanary;

public class PeopleCounterDatapoint extends DatapointBase implements Datapoint {
	public int count;

	public static PeopleCounterDatapoint fromProto(long id, String deviceEUI, java.time.Instant timestamp, OfficeCanaryProto.PeopleCounterDatapoint dp) {
		PeopleCounterDatapoint d = new PeopleCounterDatapoint();
		d.id = id;
		d.deviceEUI = deviceEUI;
		d.timestamp = timestamp;
		d.count = dp.getCount();
		return d;
	}
}
