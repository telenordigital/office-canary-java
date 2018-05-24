package com.telenordigital.officecanary;

import com.telenordigital.aviary.AviaryProto;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatapointStream {
	private static final Logger logger = Logger.getLogger(DatapointStream.class.getName());

	private final Iterator<AviaryProto.StreamDatapointsResponse> iter;

	DatapointStream(Iterator<AviaryProto.StreamDatapointsResponse> iter) {
		this.iter = iter;
	}

	public boolean hasNext() {
		try {
			return iter.hasNext();
		} catch (io.grpc.StatusRuntimeException x) {
			warn("error: {0}", x);
			return false;
		}
	}

	public Datapoint next() {
		AviaryProto.Datapoint dp = iter.next().getDatapoint();

		long id = dp.getId();
		String deviceEUI = dp.getDeviceEui();
		com.google.protobuf.Timestamp tsp = dp.getTimestamp();
		java.time.Instant timestamp = java.time.Instant.ofEpochSecond(tsp.getSeconds(), tsp.getNanos());

		com.google.protobuf.Any v = dp.getValue();

		if (v.is(AviaryProto.BootDatapoint.class)) {
			try {
				return BootDatapoint.fromProto(id, deviceEUI, timestamp, v.unpack(AviaryProto.BootDatapoint.class));
			} catch (com.google.protobuf.InvalidProtocolBufferException x) {
				warn("error: {0}", x);
				return null;
			}
		}

		if (v.is(AviaryProto.LoRaDatapoint.class)) {
			try {
				return LoRaDatapoint.fromProto(id, deviceEUI, timestamp, v.unpack(AviaryProto.LoRaDatapoint.class));
			} catch (com.google.protobuf.InvalidProtocolBufferException x) {
				warn("error: {0}", x);
				return null;
			}
		}

		if (v.is(OfficeCanaryProto.CO2Datapoint.class)) {
			try {
				return CO2Datapoint.fromProto(id, deviceEUI, timestamp, v.unpack(OfficeCanaryProto.CO2Datapoint.class));
			} catch (com.google.protobuf.InvalidProtocolBufferException x) {
				warn("error: {0}", x);
				return null;
			}
		}

		warn("unknown datapoint type: {0}", v.getTypeUrl());
		return null;
	}

	private void warn(String msg, Object... params) {
		logger.log(Level.WARNING, msg, params);
	}
}