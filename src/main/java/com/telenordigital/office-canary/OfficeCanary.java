package com.telenordigital.officecanary;

import com.telenordigital.aviary.AviaryGrpc;
import com.telenordigital.aviary.AviaryProto;
import io.grpc.Metadata;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.MetadataUtils;
import java.util.concurrent.TimeUnit;

public class OfficeCanary {
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
}
