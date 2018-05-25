package com.telenordigital.officecanary;

import com.google.protobuf.Any;
import com.telenordigital.aviary.AviaryProto;
import java.time.Instant;

public class Device {
	public String eui;
	public String name;
	public Double latitude;
	public Double longitude;
	public String type;
	public Instant lastHeardFrom;
	public ImageVersion firmwareVersion;

	static Device fromProto(AviaryProto.Device dp) throws com.google.protobuf.InvalidProtocolBufferException {
		Device d = new Device();
		d.eui = dp.getEui();
		d.name = dp.getName();
		d.latitude = Proto.toDouble(dp.getLatitude());
		d.longitude = Proto.toDouble(dp.getLongitude());
		d.type = dp.getType();
		d.lastHeardFrom = Proto.toInstant(dp.getLastHeardFrom());
		d.firmwareVersion = ImageVersion.fromProto(dp.getFirmwareVersion());
		return d;
	}
}
