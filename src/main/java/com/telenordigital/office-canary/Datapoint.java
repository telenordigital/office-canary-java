package com.telenordigital.officecanary;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.telenordigital.aviary.AviaryProto;
import java.time.Instant;

public interface Datapoint {
	static Datapoint fromProto(AviaryProto.Datapoint dp) throws com.google.protobuf.InvalidProtocolBufferException {
		long id = dp.getId();
		String deviceEUI = dp.getDeviceEui();
		Timestamp tsp = dp.getTimestamp();
		Instant timestamp = Instant.ofEpochSecond(tsp.getSeconds(), tsp.getNanos());

		Any v = dp.getValue();

		if (v.is(AviaryProto.BootDatapoint.class)) {
			return BootDatapoint.fromProto(id, deviceEUI, timestamp, v.unpack(AviaryProto.BootDatapoint.class));
		}

		if (v.is(AviaryProto.LoRaDatapoint.class)) {
			return LoRaDatapoint.fromProto(id, deviceEUI, timestamp, v.unpack(AviaryProto.LoRaDatapoint.class));
		}

		if (v.is(OfficeCanaryProto.CO2Datapoint.class)) {
			return CO2Datapoint.fromProto(id, deviceEUI, timestamp, v.unpack(OfficeCanaryProto.CO2Datapoint.class));
		}

		if (v.is(OfficeCanaryProto.ChipCap2Datapoint.class)) {
			return ChipCap2Datapoint.fromProto(id, deviceEUI, timestamp, v.unpack(OfficeCanaryProto.ChipCap2Datapoint.class));
		}

		return null;
	}
}

class DatapointBase {
	public long id;
	public String deviceEUI;
	public java.time.Instant timestamp;
}
