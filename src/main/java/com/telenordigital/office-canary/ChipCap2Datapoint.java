package com.telenordigital.officecanary;

public class ChipCap2Datapoint extends DatapointBase implements Datapoint {
	public int status;
	public double temperature;
	public double humidity;

	public static ChipCap2Datapoint fromProto(long id, String deviceEUI, java.time.Instant timestamp, OfficeCanaryProto.ChipCap2Datapoint dp) {
		ChipCap2Datapoint d = new ChipCap2Datapoint();
		d.id = id;
		d.deviceEUI = deviceEUI;
		d.timestamp = timestamp;
		d.status = dp.getStatus();
		d.temperature = dp.getTemperature();
		d.humidity = dp.getHumidity();
		return d;
	}
}
