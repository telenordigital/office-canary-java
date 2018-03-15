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
			} catch (io.grpc.StatusRuntimeException e) {
				warn("error: {0}", e);
				return false;
			}
		}

		public Datapoint next() {
			AviaryProto.StreamDatapointsResponse resp = iter.next();
			AviaryProto.Datapoint dp = resp.getDatapoint();

			Datapoint d = new Datapoint();
			d.id = dp.getId();
			d.deviceEUI = dp.getDeviceEui();
			com.google.protobuf.Timestamp ts = dp.getTimestamp();
			d.timestamp = java.time.Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());

			try {
				OfficeCanaryProto.Datapoint dv = dp.getValue().unpack(OfficeCanaryProto.Datapoint.class);
				d.appEUI = dv.getAppEui();
				d.gatewayEUI = dv.getGatewayEui();
				d.dataRate = dv.getDataRate();
				d.devAddr = dv.getDevAddr();
				d.frequency = dv.getFrequency();
				d.rssi = dv.getRssi();
				d.snr = dv.getSnr();
				d.co2PPM = dv.getCo2Ppm();
				d.co2Status = Datapoint.CO2Status.fromValue(dv.getCo2Status().getNumber());
				d.resistance = dv.getResistance();
				d.tvocPPB = dv.getTvocPpb();
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				warn("error: {0}", e);
			}

			return d;
		}
	}

	private void warn(String msg, Object... params) {
		logger.log(Level.WARNING, msg, params);
	}
}
