package com.telenordigital.officecanary;

import com.telenordigital.aviary.AviaryProto;

public class LoRaDatapoint extends DatapointBase implements Datapoint {
	public String appEUI;
	public String gatewayEUI;
	public String dataRate;
	public String devAddr;
	public double frequency;
	public int rssi;
	public double snr;

	public static LoRaDatapoint fromProto(long id, String deviceEUI, java.time.Instant timestamp, AviaryProto.LoRaDatapoint dp) {
		LoRaDatapoint d = new LoRaDatapoint();
		d.id = id;
		d.deviceEUI = deviceEUI;
		d.timestamp = timestamp;
		d.appEUI = dp.getAppEui();
		d.gatewayEUI = dp.getGatewayEui();
		d.dataRate = dp.getDataRate();
		d.devAddr = dp.getDevAddr();
		d.frequency = dp.getFrequency();
		d.rssi = dp.getRssi();
		d.snr = dp.getSnr();
		return d;
	}
}
