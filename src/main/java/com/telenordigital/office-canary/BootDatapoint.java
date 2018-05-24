package com.telenordigital.officecanary;

import com.telenordigital.aviary.AviaryProto;

public class BootDatapoint extends DatapointBase implements Datapoint {
	public ImageVersion firmwareVersion;

	public static BootDatapoint fromProto(long id, String deviceEUI, java.time.Instant timestamp, AviaryProto.BootDatapoint dp) {
		BootDatapoint d = new BootDatapoint();
		d.id = id;
		d.deviceEUI = deviceEUI;
		d.timestamp = timestamp;
		d.firmwareVersion = ImageVersion.fromProto(dp.getFirmwareVersion());
		return d;
	}
}
