package com.telenordigital.officecanary;

import com.google.protobuf.Timestamp;
import com.telenordigital.aviary.AviaryGrpc;
import com.telenordigital.aviary.AviaryProto;
import io.grpc.Metadata;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.MetadataUtils;
import java.time.Instant;
import java.util.ArrayList;
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

	public Datapoint[] getDatapoints(String eui, Instant since, Instant until) {
		AviaryProto.GetDatapointsRequest req = AviaryProto.GetDatapointsRequest.newBuilder()
			.setEui(eui)
			.setSince(instantToProto(since))
			.setUntil(instantToProto(until))
			.build();
		AviaryProto.GetDatapointsResponse resp = stub.getDatapoints(req);

		ArrayList<Datapoint> datapoints = new ArrayList<Datapoint>();
		for (AviaryProto.Datapoint d : resp.getDatapointsList()) {
			try {
				datapoints.add(Datapoint.fromProto(d));
			} catch (com.google.protobuf.InvalidProtocolBufferException x) {
				warn("error: {0}", x);
			}
		}

		return datapoints.toArray(new Datapoint[0]);
	}

	public DatapointStream streamDatapoints() {
		AviaryProto.StreamDatapointsRequest req = AviaryProto.StreamDatapointsRequest.newBuilder().build();
		return new DatapointStream(stub.streamDatapoints(req));
	}

	private static Timestamp instantToProto(Instant i) {
		return Timestamp.newBuilder().setSeconds(i.getEpochSecond()).setNanos(i.getNano()).build();
	}

	private void warn(String msg, Object... params) {
		logger.log(Level.WARNING, msg, params);
	}
}
