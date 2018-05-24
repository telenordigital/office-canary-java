package com.telenordigital.officecanary;

import com.google.protobuf.Any;
import com.telenordigital.aviary.AviaryGrpc;
import com.telenordigital.aviary.AviaryProto;
import io.grpc.Metadata;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OfficeCanary {
	private static final Logger logger = Logger.getLogger(OfficeCanary.class.getName());

	private final ManagedChannel channel;
	private final AviaryGrpc.AviaryBlockingStub stub;
	
	public OfficeCanary(String token) {
		this("oc1.aviary.services", 443, token);
	}

	public OfficeCanary(String host, int port, String token) {
		this(ManagedChannelBuilder.forAddress(host, port), token);
	}

	public OfficeCanary(ManagedChannelBuilder<?> channelBuilder, String token) {
		channel = channelBuilder.build();

		Metadata md = new Metadata();
		md.put(Metadata.Key.of("x-api-token", Metadata.ASCII_STRING_MARSHALLER), token);

		stub = MetadataUtils.attachHeaders(AviaryGrpc.newBlockingStub(channel), md);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public DatapointStream streamDatapoints() {
		AviaryProto.StreamDatapointsRequest req = AviaryProto.StreamDatapointsRequest.newBuilder().build();
		return new DatapointStream(stub.streamDatapoints(req));
	}

	public class DatapointStream {
		private final Iterator<AviaryProto.StreamDatapointsResponse> iter;

		private DatapointStream(Iterator<AviaryProto.StreamDatapointsResponse> iter) {
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
	}

	private void warn(String msg, Object... params) {
		logger.log(Level.WARNING, msg, params);
	}
}
